package com.wayfarer.wayfarer_backend.service.hike;

import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeCreateRequest;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeResponse;
import com.wayfarer.wayfarer_backend.model.Favorite;
import com.wayfarer.wayfarer_backend.model.Hike;
import com.wayfarer.wayfarer_backend.model.HikeDifficulty;
import com.wayfarer.wayfarer_backend.model.User;
import com.wayfarer.wayfarer_backend.repository.FavoriteRepository;
import com.wayfarer.wayfarer_backend.repository.HikeRepository;
import com.wayfarer.wayfarer_backend.repository.UserRepository;
import com.wayfarer.wayfarer_backend.service.auth_service.CurrentUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HikeService {

    private final HikeRepository hikeRepository;
    private final FavoriteRepository favoriteRepository;
    private final CurrentUserService currentUserService;
    private final GpxService gpxService;

    public HikeService(HikeRepository hikeRepository,
                       FavoriteRepository favoriteRepository,
                       CurrentUserService currentUserService,
                       GpxService gpxService) {
        this.hikeRepository = hikeRepository;
        this.favoriteRepository = favoriteRepository;
        this.currentUserService = currentUserService;
        this.gpxService = gpxService;
    }

    @Transactional
    public HikeResponse create(HikeCreateRequest request) {
        User creator = this.currentUserService.getCurrentUser();
        GpxService.ParsedGpx parsed = gpxService.parse(request.gpxContent());

        Hike hike = new Hike();
        hike.setName(request.name());
        hike.setDescription(request.description());
        hike.setBackToStart(request.backToStart());
        hike.setCreator(creator);
        hike.setPath(parsed.path());
        hike.setStartingPoint(parsed.startingPoint());
        hike.setDistanceMeters(parsed.distanceMeters());
        hike.setElevationGain(parsed.elevationGain());
        hike.setElevationLoss(parsed.elevationLoss());
        hike.setDurationSeconds(parsed.durationSeconds());
        hike.setDifficultyFromKmEffort();

        return toResponse(hikeRepository.save(hike), false);
    }

    @Transactional(readOnly = true)
    public List<HikeResponse> findNearby(double lat, double lon, double radiusMeters) {
        double cappedRadius = Math.min(radiusMeters, 100_000);
        return hikeRepository.findNearStartingPoint(lat, lon, cappedRadius).stream()
                .map(h -> toResponse(h, favoriteRepository.existsByUserIdAndHikeId(this.currentUserService.getCurrentUser().getId(), h.getId())))
                .toList();
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

    private HikeResponse toResponse(Hike h, boolean favorite) {
        return new HikeResponse(h.getId(), h.getName(), h.getDescription(),
                h.getCreator().getUsername(), h.getDistanceMeters(),
                h.getElevationGain(), h.getElevationLoss(), h.getDurationSeconds(),
                h.isBackToStart(), favorite);
    }
}