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
const sharedListInput = document.getElementById('sharedList');

shareSearchButton.addEventListener('click', function () {
    if (shareSearchButton.innerText === '검색') {
        shareModal.style.display = 'flex';
        shareSearchButton.innerText = '확인'

        document.getElementById('keyword').innerText = sharedListInput.value;
        console.log(sharedListInput.innerText);
    } else {
        shareModal.style.display = 'none';
        shareSearchButton.innerText = '검색'
        shareButton.style.display = 'none';
    }
});

let hoveredUser = null;

// 모든 searchedUser에 마우스 오버 이벤트 추가
const searchedUsers = document.querySelectorAll('.searchedUser');
searchedUsers.forEach(function (searchedUser) {
    searchedUser.addEventListener('mouseover', function() {
        // 마우스가 오버된 searchedUser의 좌표를 계산
        const searchedUserRect = searchedUser.getBoundingClientRect();
        const absoluteTop = searchedUserRect.top - shareModal.getBoundingClientRect().top - 41;
        const absoluteLeft = searchedUserRect.left - shareModal.getBoundingClientRect().left + 10;

        // 공유 버튼 위치 업데이트
        shareButton.style.display = 'block';
        shareButton.style.top = `${absoluteTop}px`;
        shareButton.style.left = `${absoluteLeft}px`;

        // 현재 마우스 오버된 요소를 저장
        hoveredUser = searchedUser;
    });
});

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
    sharedListInput.innerHTML = '';
    sharedList.forEach(user => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'sharedUsers[]'; // 배열 형태로 전송
        input.value = user;
        sharedListInput.appendChild(input);
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

confirmButton.addEventListener('click', function () {
    storyModal.style.display = 'none';

    if (storyModalContent.innerText === '생성한 스토리가 저장되었습니다.') {
        document.getElementById('createForm').submit();
    }

    if (storyModalContent.innerText === '스토리 생성을 취소하였습니다.') {
        location.href = 'form';
    }
});