package com.wayfarer.wayfarer_backend.service.hike;

import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeCreateRequest;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeResponse;
import com.wayfarer.wayfarer_backend.model.Favorite;
import com.wayfarer.wayfarer_backend.model.Hike;
import com.wayfarer.wayfarer_backend.model.User;
import com.wayfarer.wayfarer_backend.repository.FavoriteRepository;
import com.wayfarer.wayfarer_backend.repository.HikeRepository;
import com.wayfarer.wayfarer_backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HikeService {

    private final HikeRepository hikeRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final GpxService gpxService;

    public HikeService(HikeRepository hikeRepository, FavoriteRepository favoriteRepository,
                       UserRepository userRepository, GpxService gpxService) {
        this.hikeRepository = hikeRepository;
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.gpxService = gpxService;
    }

    @Transactional
    public HikeResponse create(HikeCreateRequest request) {
        User creator = currentUser();
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

        return toResponse(hikeRepository.save(hike), false);
    }

    @Transactional(readOnly = true)
    public List<HikeResponse> findNearby(double lat, double lon, double radiusMeters) {
        double cappedRadius = Math.min(radiusMeters, 100_000);
        return hikeRepository.findNearStartingPoint(lat, lon, cappedRadius).stream()
                .map(h -> toResponse(h, favoriteRepository.existsByUserIdAndHikeId(currentUser().getId(), h.getId())))
                .toList();
    }

    @Transactional
    public void addFavorite(Integer hikeId) {
        User user = currentUser();
        Hike hike = hikeRepository.findById(hikeId)
                .orElseThrow(() -> new IllegalArgumentException("Randonnée introuvable"));
        if (!favoriteRepository.existsByUserIdAndHikeId(user.getId(), hikeId)) {
            favoriteRepository.save(new Favorite(user, hike));
        }
    }

    @Transactional
    public void removeFavorite(Integer hikeId) {
        favoriteRepository.deleteByUserIdAndHikeId(currentUser().getId(), hikeId);
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Utilisateur authentifié introuvable"));
    }

    private HikeResponse toResponse(Hike h, boolean favorite) {
        return new HikeResponse(h.getId(), h.getName(), h.getDescription(),
                h.getCreator().getUsername(), h.getDistanceMeters(),
                h.getElevationGain(), h.getElevationLoss(), h.getDurationSeconds(),
                h.isBackToStart(), favorite);
    }
}