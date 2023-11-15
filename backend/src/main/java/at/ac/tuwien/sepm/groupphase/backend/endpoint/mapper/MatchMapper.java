package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import org.mapstruct.Mapper;

import java.util.Base64;

/**
 * JPA interface to map matches.
 */
@Mapper
public interface MatchMapper {

    /**
     * Custom mapping method to map byte[] to String.
     */
    default String mapByteArrayToString(byte[] value) {
        if (value != null) {
            return "data:image/jpeg;base64," + Base64.getMimeEncoder().encodeToString(value);
        }
        return null;
    }

}
