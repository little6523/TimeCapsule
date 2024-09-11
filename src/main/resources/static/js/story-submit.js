document.getElementById('createForm').addEventListener('submit', function() {
    // div의 내용을 숨겨진 필드에 복사
    document.getElementById('contentInput').value = document.getElementById('content').innerText;

    const searchedUsers = document.getElementsByClassName('searchedUser');
    for (let i = 0; i < searchedUsers.length; i++) {
        searchedUsers[i].value = searchedUsers[i].placeholder;
        console.log(searchedUsers.value);
    }
});

document.getElementById('createForm').addEventListener('submit', function(event) {
    event.preventDefault(); // 폼 제출을 막음
});

// 파일 업로드 필드가 비어있는지 확인
const fileInput = document.getElementById('imageUpload');
if (fileInput.files.length === 0) {
    // 파일이 선택되지 않았다면 해당 입력 필드를 제거 또는 처리
    fileInput.remove();
}

const communityToggle = document.getElementById('communityToggle');

// 체크박스 상태 변경 시 true 또는 false 값을 전송하도록 설정
communityToggle.addEventListener('change', function() {
    if (communityToggle.checked) {
        this.value = true;
    } else {
        this.value = false;
    }
});