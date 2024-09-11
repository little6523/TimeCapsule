package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.Story;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StoryDTO {

  private Long id;

  private UserDTO userDTO;

  private String title;

  private String content;

  private String dialect;

  private String speaker;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private String soundFile;

  private boolean isShared;

  //  private List<StoryShareDTO> storyShares = new ArrayList<>();

  //  private List<ImageDTO> images = new ArrayList<>();

  public Story toEntity() {

    return Story.builder()
        .user(userDTO.toEntity())
        .title(title)
        .content(content)
        .dialect(dialect)
        .speaker(speaker)
        .soundFile(soundFile)
        .isShared(isShared)
        .build();
  }

  public static StoryDTO fromEntity(Story story) {
    return StoryDTO.builder()
        .id(story.getId())
        .userDTO(UserDTO.fromEntity(story.getUser()))
        .title(story.getTitle())
        .content(story.getContent())
        .dialect(story.getDialect())
        .speaker(story.getSpeaker())
        .createdAt(story.getCreatedAt())
        .updatedAt(story.getUpdatedAt())
        .soundFile(story.getSoundFile())
        .isShared(story.isShared())
        .build();
  }
}
