const contentEditableDiv = document.getElementById('content');
const storyModal = document.getElementById('storyModal');
const saveButton = document.getElementById('save');
const editButton = document.getElementById('edit');
const cancelButton = document.getElementById('cancel');
const storyModalContent = document.getElementById('storyModalContent');
const confirmButton = document.getElementById('storyModalButton');
const shareModal = document.getElementById('shareModal');
const shareButton = document.getElementById('share');
const shareSearchButton = document.getElementById('shareSearchButton');
const search = document.getElementById('search');
const result = document.getElementById('result');
const searchedList = document.getElementById('searchedList');

shareSearchButton.addEventListener('click', searchUsers);
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
    result.style.backgroundColor = '#EAE1E1';

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

const modalSharedList = document.getElementById('modalSharedList');
let sharedList = [];

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
    modalSharedList.appendChild(tag);

    // 선택된 사용자를 sharedList에 추가
    sharedList.push(text);

    // 삭제 버튼 클릭 시 태그 삭제
    tag.querySelector('.remove-tag').addEventListener('click', function () {
        modalSharedList.removeChild(tag);
        sharedList = sharedList.filter(item => item !== text);

        // 삭제된 공유자를 sharedList에서 제거
        const index = sharedList.indexOf(text);
        if (index > -1) {
            sharedList.splice(index, 1);
        }
        updateHiddenInputs();
    });

    updateHiddenInputs();
}

// 숨겨진 input 필드에 리스트 형태로 사용자 추가
function updateHiddenInputs() {
    search.innerHTML = '';
    sharedList.forEach(user => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'sharedUsers[]'; // 배열 형태로 전송
        input.value = user;
        search.appendChild(input);
    });
}

// 공유 버튼 클릭 시 태그 추가
shareButton.addEventListener('click', function () {
    if (hoveredUser) {
        addTag(hoveredUser.innerText.trim());
    } else {
        alert('공유할 사용자를 선택하세요.');
    }
});

saveButton.addEventListener('click', function () {
    storyModalContent.innerText = '생성한 스토리가 저장되었습니다.'
    storyModal.style.display = 'block';
});

editButton.addEventListener('click', function () {
    if (this.innerText === '스토리 수정') {
        storyModalContent.innerText = '수정 모드로 변환합니다.'
        storyModal.style.display = 'block';

        saveButton.style.visibility = 'hidden';

        contentEditableDiv.contentEditable = 'true';
        contentEditableDiv.style.outline = 'none';
        this.innerText = '수정 완료';
    } else if (this.innerText === '수정 완료') {
        storyModalContent.innerText = '생성한 스토리를 수정하였습니다.'
        storyModal.style.display = 'block';

        saveButton.style.visibility = 'visible';

        contentEditableDiv.contentEditable = 'false';
        this.innerText = '스토리 수정';
    }
});

cancelButton.addEventListener('click', function () {
    storyModalContent.innerText = '스토리 생성을 취소하였습니다.'
    storyModal.style.display = 'block';
});