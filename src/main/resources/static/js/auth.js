// 메인 페이지로 인증된 사용자만 접근할 수 있도록 하는 함수
document.addEventListener("DOMContentLoaded", async () => {
    const token = getJwtToken();

    if (!token) {
        redirectToLogin();
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
                    sessionStorage.setItem('jwtToken', newAccessToken);
                }
                userId = response.headers.get('X-User-Id');
                if (userId) {
                    // 로그인 된 사용자 이름을 표시
                    updateSidebarLinks(userId); //사이드바 처리
                    updateButtons(true); //header 처리
                    document.getElementById('userId').value = userId;
                } else {
                    console.log('userId 헤더가 응답에 포함되지 않았습니다.');
                }
            } else {
                console.log('메인 페이지 접근 실패: 401 Unauthorized');
                showError(response.text());
                // redirectToLogin();
                // updateButtons(false); //header 처리
                throw new Error('Unauthorized'); // 오류를 던져 다음 then 블록이 실행되지 않도록 함
            }
        })
        .catch(error => {
            console.error('Error during fetching page:', error);
            showError("Error during fetching page");
            // redirectToLogin();
            // updateButtons(false); //header 처리
        });
});

function showError(message) {
    Swal.fire({
        icon: 'error',
        title: '입력 오류',
        text: message,
        confirmButtonText: '확인'
    }).then((result) => {
        if (result.isConfirmed) {
            redirectToLogin();
            updateButtons(false); //header 처리
        }
    })
}

// JWT 토큰을 로컬 스토리지에서 가져오는 함수
function getJwtToken() {
    return sessionStorage.getItem('jwtToken');
}


// 로그인 페이지로 리다이렉트하는 함수
function redirectToLogin() {
    window.location.href = '/login';
    sessionStorage.removeItem('jwtToken');
}

//사이드바 설정
function updateSidebarLinks(userId) {
    const links = [
        'my-story-link',
        'community-link',
        'account-management-link'
    ];

    links.forEach(linkId => {
        const link = document.getElementById(linkId);
        if (link) {
            const currentHref = link.getAttribute('href');
            const separator = currentHref.includes('?') ? '&' : '?';
            link.href = `${currentHref}${separator}userId=${userId}`;
        }
    });
}

//헤더 설정
function updateButtons(isLoggedIn) {
    const loginButton = document.querySelector('.login-button');
    const signupButton = document.querySelector('.signup-button');
    const logoutButton = document.getElementById('logout-button');

    if (isLoggedIn) {
        loginButton.style.display = 'none';
        signupButton.style.display = 'none';
        logoutButton.style.display = 'block';
    } else {
        loginButton.style.display = 'block';
        signupButton.style.display = 'block';
        logoutButton.style.display = 'none';
    }
}