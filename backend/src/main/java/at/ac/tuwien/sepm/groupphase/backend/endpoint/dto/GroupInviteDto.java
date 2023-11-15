package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

public record GroupInviteDto(
    Long groupId,
    String groupName) {
    @Override
    public Long groupId() {
        return groupId;
    }

    @Override
    public String groupName() {
        return groupName;
    }
}
