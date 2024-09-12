// 메인 페이지로 인증된 사용자만 접근할 수 있도록 하는 함수
document.addEventListener("DOMContentLoaded", async () => {
    // 페이지 로드 시 기본적으로 사이드바를 숨깁니다.
    hideSidebar();
    const token = getJwtToken();

    if (!token) {
        redirectToLogin();
        return;  // 토큰이 없으면 여기서 함수 실행을 멈춥니다.
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
                userRole = response.headers.get('X-User-Role');
                if (userId) {
                    // 로그인 된 사용자 이름을 표시
                    updateSidebarLinks(userId); //사이드바 처리
                    updateButtons(true); //header 처리
                    document.getElementById('userId').value = userId;
                    showSidebar();  // 유효한 사용자 ID가 있을 때만 사이드바를 표시합니다.
                } else {
                    console.log('userId 헤더가 응답에 포함되지 않았습니다.');
                    hideSidebar();
                }
                if (userRole) {
                    roleCheck(userRole);

                } else {
                    console.log('userRole 헤더가 응답에 포함되지 않았습니다.')
                }
            } else {
                console.log('메인 페이지 접근 실패: 401 Unauthorized');
                const errorMessage = response.text();
                showError(errorMessage);
                hideSidebar();
                // updateButtons(false); //header 처리
                throw new Error('Unauthorized'); // 오류를 던져 다음 then 블록이 실행되지 않도록 함
            }
        })
        .catch(error => {
            showError('Error during fetching page');
            console.error('Error during fetching page:', error);
            hideSidebar(); //사이드바 숨김
            // updateButtons(false); //header 처리
        });
});

function roleCheck(userRole) {
    if (userRole === 'ROLE_ADMIN') {
        console.log('관리자 권한이 확인되었습니다.');

        document.getElementById('adminButton').style.display = 'block';
    } else {
        console.log('일반 사용자입니다.');

        document.getElementById('adminButton').style.display = 'none';
    }
}

function showError(message) {
    Swal.fire({
        icon: 'error',
        title: '입력 오류',
        text: message,
        confirmButtonText: '확인'
    }).then((result) => {
        if (result.isConfirmed) {
            history.back();
        }
    })
}

// JWT 토큰을 로컬 스토리지에서 가져오는 함수
function getJwtToken() {
    return sessionStorage.getItem('jwtToken');
}

// // 로그인 페이지로 리다이렉트하는 함수
// function redirectToLogin() {
//     window.location.href = '/login';
//     sessionStorage.removeItem('jwtToken');
// }

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

// 사이드바 표시 함수
function showSidebar() {
    const sidebar = document.querySelector('.sidebar'); // 사이드바의 클래스나 ID에 맞게 수정하세요
    if (sidebar) {
        sidebar.style.display = 'block';
    }
}

// 사이드바 숨김 함수
function hideSidebar() {
    const sidebar = document.querySelector('.sidebar'); // 사이드바의 클래스나 ID에 맞게 수정하세요
    if (sidebar) {
        sidebar.style.display = 'none';
    }
}
