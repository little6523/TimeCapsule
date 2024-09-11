document.addEventListener('DOMContentLoaded', () => {
    const token = sessionStorage.getItem('jwtToken');
    if (token) {
        showError("이미 로그인 되어있습니다!");
        history.back();
        return;
    }
});

// 중복 확인 상태를 저장하는 객체
const isDuplicateCheckPassed = {
    userId: false,
    nickname: false,
    email: false
};

// 유효성 검사 상태를 저장하는 객체
const validationState = {
    userId: false,
    password: false,
    confirmPassword: false,
    nickname: false,
    email: false
};

// 유효성 검사 및 중복 검사 통합 함수
function checkDuplicateAndValidate(field) {
    // 유효성 검사 수행
    const errorMessage = validateField(field);

    // 유효성 검사 결과 메시지 표시
    if (errorMessage != "") {
        Swal.fire({
            icon: 'error',
            title: '유효성 검사 실패',
            text: errorMessage,
            confirmButtonText: '확인'
        });
        return;
    }

    // 유효성 검사가 통과된 경우 중복 검사 진행
    const value = document.getElementById(field).value;

    fetch('/api/users/check-duplicate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            field: field,
            value: value
        })
    })
        .then(response => response.json())
        .then(isDuplicate => {
            if (isDuplicate) {
                Swal.fire({
                    icon: 'error',
                    title: '중복 확인',
                    text: `${field === 'userId' ? '아이디가' : field === 'email' ? '이메일이' : '닉네임이'} 이미 사용 중입니다.`,
                    confirmButtonText: '확인'
                });
                isDuplicateCheckPassed[field] = false; // 중복 검사 실패로 상태 갱신
            } else {
                Swal.fire({
                    icon: 'success',
                    title: '중복 확인',
                    text: '사용 가능한 값입니다.',
                    confirmButtonText: '확인'
                });
                isDuplicateCheckPassed[field] = true; // 중복 검사 성공으로 상태 갱신
            }
            isDuplicateCheckPassed['userId'] = true;
            updateSubmitButtonState(); // 버튼 상태 업데이트
        })
        .catch(error => {
            console.error('Error:', error);
            Swal.fire({
                icon: 'error',
                title: '오류',
                text: '중복 확인에 실패했습니다. 다시 시도해주세요.',
                confirmButtonText: '확인'
            });
        });
}

// 유효성 검사 함수
function validateField(field) {
    const value = document.getElementById(field).value;
    let errorMessage = "";
    let isValid = true;

    switch (field) {
        case 'userId':
            const userIdRegex = /^[a-zA-Z0-9]{4,20}$/;
            if (!userIdRegex.test(value)) {
                errorMessage = '아이디는 대소문자나 숫자를 포함한 4-20자리로 입력하세요.';
                isValid = false;
            }
            break;
        case 'password':
            const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@#!~$%^&*()_+=<>?])?\S{6,20}$/;
            if (!passwordRegex.test(value)) {
                errorMessage = '비밀번호는 알파벳과 숫자를 포함한 6-20자리의 비밀번호를 입력하세요.';
                isValid = false;
            }
            break;
        case 'nickname':
            if (value.trim() === "") {
                errorMessage = '닉네임을 입력해주세요.';
                isValid = false;
            }
            break;
        case 'email':
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(value)) {
                errorMessage = '유효한 이메일 주소를 입력해주세요.';
                isValid = false;
            }
            break;
    }

    // 유효성 검사 상태 업데이트
    validationState[field] = isValid;
    return errorMessage; // 유효성 검사 결과 반환
}

// 비밀번호 확인 일치 여부 검사
function validatePasswordMatch() {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const mismatchElement = document.getElementById('passwordMismatch');

    // 서버로 비밀번호 검증 요청 보내기
    fetch('/api/users/password-match', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            userId: '',
            password: password,
            confirmPassword: confirmPassword,
            nickname: '',
            email: ''
        })
    })
        .then(response => response.json())
        .then(isMatch => {
            // 비밀번호가 일치하면 메시지 업데이트
            if (isMatch) {
                mismatchElement.textContent = '비밀번호가 일치합니다.';
                mismatchElement.style.display = 'block';
                mismatchElement.style.color = 'green'; // 일치하는 경우 글씨 색상을 초록색으로 변경
                validationState['confirmPassword'] = true;
            } else {
                // 비밀번호가 일치하지 않으면 메시지 업데이트
                mismatchElement.textContent = '비밀번호가 일치하지 않습니다.';
                mismatchElement.style.display = 'block';
                mismatchElement.style.color = 'red'; // 일치하지 않는 경우 글씨 색상을 빨간색으로 변경
                validationState['confirmPassword'] = false;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            Swal.fire({
                icon: 'error',
                title: '오류',
                text: '비밀번호 확인에 실패했습니다. 다시 시도해주세요.',
                confirmButtonText: '확인'
            });
        });
}

// 중복 확인 후 버튼 활성화 상태를 업데이트하는 함수
function updateSubmitButtonState() {
    const allPassed = Object.values(isDuplicateCheckPassed).every(value => value);
    document.querySelector('.action-button[type="submit"]').disabled = !allPassed; // 가입하기 버튼 활성화/비활성화
}

// /signUp 에서 bindinfresult에 에러 사항이 발견 될 경우, signup페이지로 다시 돌아가면서 표시할 모달 창 구현
function showError() {
    // 에러 메시지 요소가 있을 경우, 모달창을 띄움
    const errorMessage = document.getElementById('error-message');

    if (errorMessage && errorMessage.innerText.trim() !== "") {
        Swal.fire({
            icon: 'error',
            title: '입력 오류',
            text: errorMessage.innerText,
            confirmButtonText: '확인'
        });
    }
}
window.onload = showError;  // 페이지 로드 시 showError 함수 실행

// 폼 제출 이벤트 리스너 추가
document.getElementById('signupForm').addEventListener('submit', function (event) {
    // 모든 필드를 검사하여 validationState를 업데이트
    const fields = Object.keys(validationState);
    fields.forEach(field => validateField(field));

    // 유효하지 않은 필드를 찾아 에러 메시지 출력
    const invalidField = fields.find(field => !validationState[field]);
    if (invalidField) {
        event.preventDefault(); // 폼 제출 막기
        const errorMessage = validateField(invalidField); // 해당 필드의 에러 메시지 가져오기
        Swal.fire({
            icon: 'error',
            title: '입력 오류',
            text: errorMessage,
            confirmButtonText: '확인'
        });
    }
});