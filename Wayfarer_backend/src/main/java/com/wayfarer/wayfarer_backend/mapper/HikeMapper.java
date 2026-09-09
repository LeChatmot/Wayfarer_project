package com.wayfarer.wayfarer_backend.mapper;

import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeResponse;
import com.wayfarer.wayfarer_backend.dto.hike_dto.StartingPointResponse;
import com.wayfarer.wayfarer_backend.model.Hike;
import com.wayfarer.wayfarer_backend.model.Role;
import com.wayfarer.wayfarer_backend.model.User;
import com.wayfarer.wayfarer_backend.repository.FavoriteRepository;
import com.wayfarer.wayfarer_backend.service.auth_service.CurrentUserService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class HikeMapper {

    private final FavoriteRepository favoriteRepository;
    private final CurrentUserService currentUserService;

    public HikeMapper(
            FavoriteRepository favoriteRepository,
            CurrentUserService currentUserService
    ) {
        this.favoriteRepository = favoriteRepository;
        this.currentUserService = currentUserService;
    }

    public HikeResponse toDto(Hike hike) {
        boolean favorite = currentUserService.findCurrentUser()
                .map(user -> favoriteRepository.existsByUserIdAndHikeId(
                        user.getId(),
                        hike.getId()
                ))
                .orElse(false);

        Coordinate coordinate = hike.getStartingPoint().getCoordinate();

        StartingPointResponse startingPoint = new StartingPointResponse(
                coordinate.getY(),
                coordinate.getX(),
                coordinate.getZ()
        );

        User creator = null;
        if (hike.getCreator() == null){
         creator = new User("null", "null", "Anonymous", Role.ROLE_USER);
        } else {
            creator = hike.getCreator();
        }

        return new HikeResponse(
                hike.getId(),
                hike.getName(),
                hike.getDescription(),
                hike.isBackToStart(),
                hike.getDistanceMeters(),
                hike.getElevationGain(),
                hike.getElevationLoss(),
                startingPoint,
                hike.getStartingPointName(),
                creator.getUsername(),
                hike.getDurationSeconds(),
                hike.getHikeDifficulty(),
                hike.getPreviewImageLightUrl(),
                hike.getPreviewImageDarkUrl(),
                favorite
        );
    }

    public List<double[]> mapPath(LineString path) {
        if (path == null) {
            return List.of();
        }
        return Arrays.stream(path.getCoordinates())
                .map(c -> new double[]{c.getY(), c.getX(), c.getZ()})
                .toList();
    }
}
