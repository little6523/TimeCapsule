let userId;

// JWT 토큰을 로컬 스토리지에서 가져오는 함수
function getJwtToken() {
    return localStorage.getItem('jwtToken');
}

// 메인 페이지로 인증된 사용자만 접근할 수 있도록 하는 함수
document.addEventListener("DOMContentLoaded", async () => {
    const token = getJwtToken();

    if (!token) {
        // showNotLoggedInView();
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
                const newAccessToken = response.headers.get('Authorization').split(' ')[1];
                if (newAccessToken) {
                    // 새로운 Access Token을 로컬 스토리지나 메모리에 저장
                    localStorage.setItem('jwtToken', newAccessToken);
                }
                const userId = response.headers.get('X-User-Id');
                console.log(userId);
                if (userId) {
                    // 로그인 된 사용자 이름을 표시
                } else {
                    console.log('userId 헤더가 응답에 포함되지 않았습니다.');
                }
            } else {
                console.log('메인 페이지 접근 실패: 401 Unauthorized');
                redirectToLogin();
                throw new Error('Unauthorized'); // 오류를 던져 다음 then 블록이 실행되지 않도록 함
            }
        })
        .catch(error => {
            console.error('Error during fetching page:', error);
            redirectToLogin();
        });
});

// 로그인 페이지로 리다이렉트하는 함수
function redirectToLogin() {
    window.location.href = '/login';
}