// 폼 데이터를 PUT 요청으로 서버로 전송
window.submitForm = function () {
    // const storyId = [[${story.id}]];
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').innerText;
    const isCommunityPost = document.getElementById('communityToggle').checked;
    const files = imageUploadInput.files;
    const userId = document.getElementById('userId').value;

    // 제목을 입력하지 않을 시 오류 발생
    if (!title || title.trim() === "") {
        alert("제목을 입력해 주세요.");
        return;
    }

    // 수정 요청할 데이터 추가
    const formData = new FormData();
    formData.append("storyId", storyId);
    formData.append("title", title);
    formData.append("content", content);
    formData.append("isShared", isCommunityPost);
    formData.append("userId", userId);

    if (sharedList.length > 0) {
        sharedList.forEach(sharedUser => {
            formData.append('sharedWithUsers', sharedUser);
        });
    } else {
        formData.append('sharedWithUsers', []);
    }

    if (deletedImages.length > 0) {
        deletedImages.forEach(image => {
            formData.append('deletedImages', image);
        });
    } else {
        formData.append('deletedImages', []);
    }

    if (files.length > 0) {
        Array.from(files).forEach(file => {
            formData.append('images', file);
        });
    } else {
        formData.append('images', new Blob());
    }

    fetch(`/stories/${storyId}`, {
        method: 'PUT',
        body: formData
    }).then(async response => {
        const message = await response.text();
        if (response.ok) {
            alert(message);
            window.location.href = `/stories/${storyId}`;
        } else {
            alert(message);
            console.log(message);
        }
    }).catch(error => {
        console.error('Error:', error);
        alert('스토리 수정 중 오류가 발생했습니다. 다시 시도해주세요.');
    });
};