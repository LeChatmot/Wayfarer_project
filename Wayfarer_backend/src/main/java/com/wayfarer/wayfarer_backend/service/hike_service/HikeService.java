package com.wayfarer.wayfarer_backend.service.hike_service;

import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeCreateRequest;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeResponse;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeSearchCriteria;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeUpdateRequest;
import com.wayfarer.wayfarer_backend.imaging.HikePreviewImageGenerator;
import com.wayfarer.wayfarer_backend.mapper.HikeMapper;
import com.wayfarer.wayfarer_backend.model.*;
import com.wayfarer.wayfarer_backend.repository.FavoriteRepository;
import com.wayfarer.wayfarer_backend.repository.HikeRepository;
import com.wayfarer.wayfarer_backend.repository.specification.HikeSpecifications;
import com.wayfarer.wayfarer_backend.service.auth_service.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import org.locationtech.jts.geom.LineString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Optional;

@Service
public class HikeService {

    private Logger log = LoggerFactory.getLogger(HikeService.class);

    private final HikeRepository hikeRepository;
    private final FavoriteRepository favoriteRepository;
    private final CurrentUserService currentUserService;
    private final GpxService gpxService;
    private HikePreviewImageGenerator previewImageGenerator;
    private HikeMapper hikeMapper;

    public HikeService(HikeRepository hikeRepository,
                       FavoriteRepository favoriteRepository,
                       CurrentUserService currentUserService,
                       GpxService gpxService, HikePreviewImageGenerator previewImageGenerator, HikeMapper hikeMapper) {
        this.hikeRepository = hikeRepository;
        this.favoriteRepository = favoriteRepository;
        this.currentUserService = currentUserService;
        this.gpxService = gpxService;
        this.previewImageGenerator = previewImageGenerator;
        this.hikeMapper = hikeMapper;
    }

    @Transactional
    public HikeResponse create(HikeCreateRequest request) {
        User creator = this.currentUserService.getCurrentUser();
        ParsedGpx parsed = gpxService.parse(request.gpxContent());

        Hike hike = new Hike();
        hike.setName(request.name());
        hike.setCreatedAt(Instant.now());
        hike.setDescription(request.description());
        hike.setBackToStart(request.backToStart());
        hike.setCreator(creator);
        hike.setPath(parsed.path());
        hike.setStartingPoint(parsed.startingPoint());
        hike.setStartingPointName(parsed.startingPointName());
        hike.setDistanceMeters(parsed.distanceMeters());
        hike.setElevationGain(parsed.elevationGain());
        hike.setElevationLoss(parsed.elevationLoss());
        hike.setDurationSeconds(parsed.durationSeconds());
        this.setDifficultyFromKmEffort(hike);

        Hike saved = hikeRepository.save(hike);

        Integer hikeId = saved.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                try {
                    previewImageGenerator.generateAndAttach(hikeId);
                } catch (Exception e) {
                    log.warn("Génération de l'aperçu impossible pour la randonnée {}", hikeId, e);
                }
            }
        });


        return this.hikeMapper.toDto(saved);
    }

    public Hike getHike(Integer hikeId){
        return this.hikeRepository.findHikeById(hikeId);
    }

    public Page<Hike> search(HikeSearchCriteria criteria, Pageable pageable) {
        return hikeRepository.findAll(HikeSpecifications.withCriteria(criteria), pageable);
    }

    @Transactional
    public void addFavorite(Integer hikeId) {
        User user = this.currentUserService.getCurrentUser();
        Hike hike = hikeRepository.findById(hikeId)
                .orElseThrow(() -> new IllegalArgumentException("Randonnée introuvable"));
        if (!favoriteRepository.existsByUserIdAndHikeId(user.getId(), hikeId)) {
            favoriteRepository.save(new Favorite(user, hike));
        }
    }

    @Transactional
    public void removeFavorite(Integer hikeId) {
        favoriteRepository.deleteByUserIdAndHikeId(this.currentUserService.getCurrentUser().getId(), hikeId);
    }

    @Transactional
    public HikeResponse updateHike(Integer id, HikeUpdateRequest request) throws AccessDeniedException {
        Hike hike = hikeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Randonnée introuvable"));

        User currentUser = currentUserService.getCurrentUser();
        boolean isAdmin = currentUser.getRole().equals(Role.ROLE_ADMIN);

        if (!hike.getCreator().equals(currentUser) && !isAdmin) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cette randonnée");
        }

        hike.setName(request.name());
        hike.setDescription(request.description());

        Hike savedHike = hikeRepository.save(hike);
        return hikeMapper.toDto(savedHike);
    }

    @Transactional
    public void deleteHike(Integer hikeId){
        if (this.isUserCreatorOfHike(hikeId) || this.currentUserService.isAdmin()){
            hikeRepository.deleteById(hikeId);
        }
    }

    public boolean isUserCreatorOfHike(Integer hikeId){
        Optional<User> user = this.currentUserService.findCurrentUser();
        Hike hike = this.hikeRepository.findHikeById(hikeId);
        return user.filter(value -> hike.getCreator().equals(value)).isPresent();
    }

    public Page<Hike> searchUserHikes(int page, int size){
        User user = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return hikeRepository.findAllByCreatorEquals(user, pageable);
    }

    public Page<Hike> getFavoritesByCurrentUser(int page, int size) {
        User user = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Favorite> favorites = favoriteRepository.findAllByUserId(user.getId(), pageable);
        return favorites.map(Favorite::getHike);
    }

    public LineString getPathFromHike(Integer hikeId){
        Hike hike = this.hikeRepository.findHikeById(hikeId);
        return hike.getPath();
    }

    public void setDifficultyFromKmEffort(Hike hike){
        double kmEffort = hike.getDistanceMeters()/1000 + hike.getElevationGain()/100 + hike.getElevationLoss()/400;
        if (kmEffort < 10L){
            hike.setHikeDifficulty(HikeDifficulty.EASY);
        } else if (kmEffort < 30L){
            hike.setHikeDifficulty(HikeDifficulty.MEDIUM);
        }  else if (kmEffort < 50L){
            hike.setHikeDifficulty(HikeDifficulty.HARD);
        } else {
            hike.setHikeDifficulty(HikeDifficulty.VERY_HARD);
        }
    }
}