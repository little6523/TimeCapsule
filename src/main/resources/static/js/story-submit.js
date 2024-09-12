document.getElementById('createForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const searchedUsers = document.getElementsByClassName('searchedUser');
    for (let i = 0; i < searchedUsers.length; i++) {
        searchedUsers[i].value = searchedUsers[i].placeholder;
        console.log(searchedUsers.value);
    }
});

const communityToggle = document.getElementById('communityToggle');

// 체크박스 상태 변경 시 true 또는 false 값을 전송하도록 설정
communityToggle.addEventListener('change', function() {
    if (communityToggle.checked) {
        console.log('커뮤니티 게시 할래요!');
        this.value = true;
    } else {
        console.log('커뮤니티 게시 안 할래요!');
        this.value = false;
    }
});