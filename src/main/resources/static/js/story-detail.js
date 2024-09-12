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

    tagInputContainer.insertBefore(tag, tagInput);
}

// 기존 공유자 리스트를 태그 형태로 출력
existingSharedList.forEach(function (shared) {
    addTag(shared.userDTO.nickname);
    sharedList.push(shared.userDTO.nickname); // sharedList 배열에 추가
});

// 페이지 번호를 유지한 스토리 목록 페이지로 이동하는 함수
function goToStoryList() {
    const pageNumber = sessionStorage.getItem('currentPage') || 1;
    const currentURL = new URL(window.location.href);
    console.log(pageNumber);
    console.log(currentURL);

    // 스토리 ID(숫자) 제거
    const pathname = currentURL.pathname.replace(/\/\d+$/, '');

    currentURL.pathname = pathname;
    currentURL.searchParams.set('page', pageNumber);
    currentURL.searchParams.set('userId', userId);

    window.location.href = currentURL.toString();
}