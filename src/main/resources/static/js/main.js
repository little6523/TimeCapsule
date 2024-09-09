document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem('jwtToken');
    console.log('저장된 JWT 토큰:', token);

    if (!token) {
        showNotLoggedInView();
        return;
    }

    // JWT 토큰을 포함하여 /main으로 GET 요청
    fetch('/valid-token', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token, // JWT 토큰을 헤더에 포함
        }
    })
        .then(response => {
            if (response.ok) {
                // 응답 헤더에서 X-User-Id 헤더를 추출
                const newAccessToken = response.headers.get('Authorization').split(' ')[1];
                if (newAccessToken) {
                    // 새로운 Access Token을 로컬 스토리지나 메모리에 저장
                    localStorage.setItem('accessToken', newAccessToken);
                }
                console.log(response.headers);
                const userId = response.headers.get('X-User-Id');
                if (userId) {
                    showLoggedInView(userId); // 로그인 된 사용자 이름을 표시
                } else {
                    console.log('userId 헤더가 응답에 포함되지 않았습니다.');
                    showNotLoggedInView();
                }
            } else {
                console.log('메인 페이지 접근 실패: 401 Unauthorized');
                showNotLoggedInView();
                throw new Error('Unauthorized'); // 오류를 던져 다음 then 블록이 실행되지 않도록 함
            }
        })
        .catch(error => {
            console.error('Error during fetching main page:', error);
            showNotLoggedInView();
        });
});

function showNotLoggedInView() {
    document.getElementById('not-logged-in').style.display = 'block';
    document.getElementById('logged-in').style.display = 'none';
}

function showLoggedInView(userId) {
    document.getElementById('not-logged-in').style.display = 'none';
    document.getElementById('logged-in').style.display = 'block';
    document.getElementById('user-id').textContent = userId; // 사용자 이름 표시
}

// // 로그아웃 버튼 클릭 시 JWT 토큰 삭제
// document.getElementById('logoutButton')?.addEventListener('click', () => {
//     localStorage.removeItem('jwtToken'); // 토큰 삭제
//     window.location.href = '/login'; // 로그인 페이지로 리다이렉트
// });