const search = document.getElementById('sharedList');
const result = document.getElementById('result');
const searchedList = document.getElementById('searchedList');
const shareModal = document.getElementById('shareModal');
const shareButton = document.getElementById('share');
const tagInputContainer = document.querySelector('.tag-input-container');
window.sharedList = [];

search.addEventListener('keydown', function (event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        searchUsers();
    }
});

function searchUsers() {
    // 이전 검색 결과 비우기
    searchedList.innerHTML = '';

    result.innerText = "검색 결과";

    // AJAX 요청을 통해 검색된 결과 가져오기
    fetch(`/stories/search?keyword=${search.value}&userId=${userId}`)
        .then(response => response.json())
        .then(users => {

            if (users === null || users.length === 0) {
                const userDiv = document.createElement('div');
                userDiv.classList.add('searchedUser');
                userDiv.textContent = '검색 결과 없음';
                searchedList.appendChild(userDiv);

                return;
            }

            shareModal.style.display = 'flex';

            // 배열이 아니면 배열로 변환
            if (!Array.isArray(users)) {
                users = [users];
            }

            // 검색된 유저 목록을 배열로 처리
            users.forEach(user => {
                const userDiv = document.createElement('div');
                userDiv.classList.add('searchedUser');
                userDiv.textContent = user;

                // searchedList에 div 추가
                searchedList.appendChild(userDiv);

                // 여기서 마우스 오버 이벤트 추가
                userDiv.addEventListener('mouseover', function () {
                    // 마우스가 오버된 searchedUser의 좌표를 계산
                    const searchedUserRect = userDiv.getBoundingClientRect();
                    const absoluteTop = searchedUserRect.top - shareModal.getBoundingClientRect().top - 41;
                    const absoluteLeft = searchedUserRect.left - shareModal.getBoundingClientRect().left + 10;

                    // 공유 버튼 위치 업데이트
                    shareButton.style.display = 'block';
                    shareButton.style.top = `${absoluteTop}px`;
                    shareButton.style.left = `${absoluteLeft}px`;

                    // 현재 커서가 위치한 요소를 저장
                    hoveredUser = userDiv;
                });
            });

        })
        .catch(error => console.error('Error:', error));
}


// 태그 추가 함수
function addTag(text) {
    if (sharedList.includes(text)) {
        alert('이미 추가된 사용자입니다.');
        return;
    }

    const tag = document.createElement('span');
    tag.className = 'tag';
    tag.innerHTML = `${text} <i class="remove-tag">×</i>`;

    // 태그를 modalSharedList에 추가
    // tagInputContainer.appendChild(tag);
    tagInputContainer.insertBefore(tag, search);

    // 선택된 사용자를 sharedList에 추가
    sharedList.push(text);

    // 삭제 버튼 클릭 시 태그 삭제
    tag.querySelector('.remove-tag').addEventListener('click', function () {
        tagInputContainer.removeChild(tag);
        sharedList = sharedList.filter(item => item !== text);

        // 삭제된 공유자를 sharedList에서 제거
        const index = sharedList.indexOf(text);
        if (index > -1) {
            sharedList.splice(index, 1);
        }
    });
}

// 공유 버튼 클릭 시 태그 추가
shareButton.addEventListener('click', function () {
    if (hoveredUser) {
        addTag(hoveredUser.innerText.trim());
        search.value = '';
    } else {
        alert('공유할 사용자를 선택하세요.');
    }
});

existingSharedList.forEach(function (shared) {
    addTag(shared.userDTO.nickname);
    sharedList.push(shared.userDTO.nickname); // sharedList 배열에 추가
});