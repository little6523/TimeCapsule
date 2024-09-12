// currentUser, existingShareList html 내 인라인 선언
// 공유자 ID를 태그 형태로 추가 및 삭제
const tagInputContainer = document.querySelector('.tag-input-container');
const tagInput = document.querySelector('.tag-input');
window.sharedList = [];

tagInput.addEventListener('keypress', function (e) {
    if (e.key === 'Enter' && tagInput.value.trim() !== '') {
        e.preventDefault();
        const newSharedUser = tagInput.value.trim();

        // 본인 ID와 동일한 공유자 ID일 경우
        if (newSharedUser === currentUser) {
            alert('본인에게는 공유할 수 없습니다.');
            tagInput.value = '';
            return;
        }

        addTag(newSharedUser);
        // 새로운 공유자를 sharedList에 추가
        sharedList.push(newSharedUser);
        tagInput.value = '';
    }
});

function addTag(text) {
    const tag = document.createElement('span');
    tag.className = 'tag';
    tag.innerHTML = `${text}`;

    if (isEditMode) {
        tag.innerHTML = `${text} <i class="remove-tag">×</i>`;
        tag.querySelector('.remove-tag').addEventListener('click', function () {
            tagInputContainer.removeChild(tag);
            // 삭제된 공유자를 shareList에서 제거
            const index = sharedList.indexOf(text);
            if (index > -1) {
                sharedList.splice(index, 1);
            }
        });
    }
    tagInputContainer.insertBefore(tag, tagInput);
}

// 기존 공유자 리스트를 태그 형태로 출력
existingSharedList.forEach(function (shared) {
    addTag(shared.userDTO.nickname);
    sharedList.push(shared.userDTO.nickname); // sharedList 배열에 추가
});