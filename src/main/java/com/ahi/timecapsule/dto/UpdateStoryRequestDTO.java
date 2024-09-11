package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.entity.StoryShare;
import com.ahi.timecapsule.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UpdateStoryRequestDTO {

  private String title;
  private String content;
  //  private List<String> imageUrls;
  private List<String> sharedWithUsers;

  @JsonProperty("isShared")
  private boolean isShared;

  /**
   * DTO -> Story 엔티티 변환 메서드
   *
   * @param existingStory 기존 Story 객체
   * @param users 공유할 사용자 목록
   * @return 업데이트된 Story 객체
   */
  public Story toEntity(Story existingStory, List<User> users) {

    //    List<Image> images = imageUrls.stream()
    //            .map(url -> Image.builder().story(existingStory).url(url).build())
    //            .toList();

    List<StoryShare> storyShares =
        users.stream()
            .map(user -> StoryShare.builder().story(existingStory).user(user).build())
            .toList();

    existingStory.updateStory(
        this.title, this.content, this.isShared, storyShares
        //            images
        );

    return existingStory;
  }
}
