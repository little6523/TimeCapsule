//API URL 상수로 정의
const API_URL = {
    USER_SELECT: '/users/info',
    USER_UPDATE : '/users/update',
    USER_DELETE : '/users/delete',
    CHECK_NICKNAME: '/users/checkDuplicateNickname',
    CHECK_EMAIL: '/users/checkDuplicateEmail',
    LOGIN_PAGE: '/login'
};

document.addEventListener('DOMContentLoaded', () => {
    //모달 요소
    const modal = document.getElementById('modal');
    const confirmModal = document.getElementById('confirmModal');
    const openModalButton = document.getElementById('openModal');

    //수정 요소
    const updateForm = document.getElementById('updateUserForm');
    const updateButton = document.querySelector('.btn-update-user');
    const inputs = updateForm.querySelectorAll('input:not(#id)'); //id는 수정 항목에서 제외
    const userIdInput = document.getElementById('id');
    const providerInput = document.getElementById('provider');
    const passwordInput = document.getElementById('password');
    const newPasswordInput = document.getElementById('newPassword');
    const nicknameInput = document.getElementById('nickname');
    const emailInput = document.getElementById('email');
    const nicknameResult = document.getElementById('nicknameResult');
    const emailResult = document.getElementById('emailResult');

    //닉네임, 이메일 상태 값
    let isNicknameValid = true;
    let isEmailValid = true;

    //OAuth2.0 로그인 식별 상태 값
    const isOAuth2User = providerInput && providerInput.value !== '';

    //form 안에 input(id 제외) 요소 유효성 체크 함수
    const validateInputs = () => {
        let isValid = true; //유효성 체크 상태 값

        //기본 로그인 경우만 비밀번호 유효성 검사
        if(!isOAuth2User) {
            //비밀번호 유효성 검사
            if (passwordInput.value && !newPasswordInput.value) {
                modalUtils.showAlert('새 비밀번호를 입력해주세요.', () => { newPasswordInput.focus() });
                isValid = false;
            } else if (!passwordInput.value && newPasswordInput.value) {
                modalUtils.showAlert('기존 비밀번호를 입력해주세요.', () => { passwordInput.focus() });
                isValid = false;
            } else if (passwordInput.value && newPasswordInput.value) {
                if (passwordInput.value === newPasswordInput.value) {
                    modalUtils.showAlert('기존 비밀번호와 새 비밀번호가 같습니다. 다른 비밀번호를 입력해주세요.', () => { newPasswordInput.focus() });
                    isValid = false;
                }
            }
        }

        //닉네임, 이메일 유효성 검사
        if (!nicknameInput.value.trim()) {
            modalUtils.showAlert('닉네임을 입력하세요.', () => { nicknameInput.focus() });
            isValid = false;
        }

        if (!emailInput.value.trim()) {
            modalUtils.showAlert('이메일을 입력하세요.', () => { emailInput.focus() });
            isValid = false;
        }

        if (!emailInput.value.includes('@')) {
            modalUtils.showAlert('올바른 이메일 형식이 아닙니다.', () => { emailInput.focus() });
            isValid = false;
        }
        //닉네임, 이메일 중복 체크
        if (!isNicknameValid) {
            modalUtils.showAlert('이미 사용 중인 닉네임입니다.', () => { nicknameInput.focus() });
            isValid = false;
        }

        if (!isEmailValid) {
            modalUtils.showAlert('이미 사용 중인 이메일입니다.', () => { emailInput.focus() });
            isValid = false;
        }

        return isValid;
    }

    //회원 정보 수정
    const saveUserInfo = async () => {
        const formData = new FormData(updateForm);
        const userUpdateData = Object.fromEntries(formData.entries()); //form 안에 있는 모든 데이터를 객체 데이터로 변환

        //input value 값이 '' 인 경우 수정 항목에서 제외
        Object.keys(userUpdateData).forEach(key => {
            if (userUpdateData[key] === '') {
                delete userUpdateData[key];
            }
        });

        try {
            let response = await fetch(API_URL.USER_UPDATE, {
                method: 'PATCH',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(userUpdateData)
            });

            let responseBody = await response.json();

            if (!response.ok) {
                if(responseBody.success === false) {
                    modalUtils.handlePasswordError(responseBody.message);
                } else {
                    throw new Error('사용자 정보 업데이트 처리 중 오류가 발생했습니다.');
                }
                return;
            }

            if (responseBody) {
                modalUtils.showAlert(responseBody.message, () => {
                    inputs.forEach(input => input.setAttribute('readonly', ''));
                    updateButton.textContent = '수정';
                    window.location.href = `${API_URL.USER_SELECT}?userId=${responseBody.data}`;
                });
            }
        } catch (error) {
            modalUtils.showAlert(error.message, () => { window.location.reload() });
        }
    }

    //회원 탈퇴
    const deleteUser = async () => {
        let userPassword = document.getElementsByName('modalPassword')[0].value; //modal 비밀번호 input 값

        //비밀번호 유효성 체크
        if (!isOAuth2User && userPassword.trim() === '') {
            modalUtils.handlePasswordError('비밀번호를 입력해 주세요.');
            return;
        }

        try {
            let response = await fetch(API_URL.USER_DELETE, {
                method: 'DELETE',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({ password : userPassword, id : userIdInput.value })
            });

            let responseBody = await response.json(); //서버로부터 넘어온 데이터를 JSON 형식으로 파싱

            if (!response.ok) {
                if (responseBody.success === false) { //400에러
                    modalUtils.handlePasswordError(responseBody.message);
                } else { //서버 오류 발생 시 catch 문으로 예외 던짐(500에러)
                    throw new Error('서버 오류가 발생하였습니다.');
                }
                return;
            }

            if (responseBody.success === true) { //200
                modalUtils.showAlert(responseBody.message, () => {
                    localStorage.removeItem('jwtToken');
                    localStorage.removeItem('accessToken');
                    window.location.href = API_URL.LOGIN_PAGE;
                });
            }

        } catch (error) { //서버 오류 발생
            modalUtils.showAlert(error.message, () => { window.location.reload() });
        }
    }

    //수정 버튼 클릭 시 이벤트 리스너
    updateButton.addEventListener('click', () => {
        if(updateButton.textContent === '수정') {
            inputs.forEach(input => {
                //OAuth2.0인 경우 이메일 readonly 유지
                if (!(isOAuth2User && input.name === 'email')) {
                    input.removeAttribute('readonly');
                }
            }); //inputs의 모든 input 태그를 disabled 비활성화
            updateButton.textContent = '저장';
        } else {
            if(validateInputs()) {
                saveUserInfo();
            }
        }
    });

    //닉네임 중복 체크 이벤트 리스너
    nicknameInput.addEventListener('input', async () => {

        //저장 버튼일 시에만 중복 체크, 수정 버튼 시에는 기능 막음
        if(nicknameInput.value.trim() !== '' && updateButton.textContent === '저장') {

            //기존 닉네임과 사용자가 입력한 닉네임이 같을 경우 처리
            if (nicknameInput.value === nicknameInput.defaultValue) {
                isNicknameValid = true;
                nicknameResult.textContent = '현재 사용 중인 닉네임입니다.';
                nicknameResult.style.color = 'green';
                return;
            }

            try {
                let response = await fetch(`${API_URL.CHECK_NICKNAME}?nickname=${nicknameInput.value}`);
                let responseBody = await response.json();
                isNicknameValid = !responseBody; //true 이면 닉네임이 중복임으로 ! 사용

                if (isNicknameValid) {
                    nicknameResult.textContent = '사용 가능한 닉네임입니다.';
                    nicknameResult.style.color = 'green';
                } else {
                    nicknameResult.textContent = '이미 사용 중인 닉네임입니다.';
                    nicknameResult.style.color = 'red';
                }
            } catch (error) {
                console.error('닉네임 중복 체크 중 오류 발생 : ', error);
                modalUtils.showAlert('닉네임 중복 체크 중 오류가 발생했습니다.', () => { window.location.reload() });
            }
        }
    });

    //이메일 중복 체크 이벤트 리스너
    emailInput.addEventListener('input', async () => {
        //저장 버튼일 시에만 중복 체크, 수정 버튼 시에는 기능 막음
        if(emailInput.value.trim() !== '' && updateButton.textContent === '저장') {

            if (emailInput.value === emailInput.defaultValue) {
                isEmailValid = true;
                emailResult.textContent = '현재 사용 중인 이메일입니다.';
                emailResult.style.color = 'green';
                return;
            }

            try {
                let response = await fetch(`${API_URL.CHECK_EMAIL}?email=${emailInput.value}`);
                let responseBody = await response.json();
                isEmailValid = !responseBody;

                if (isEmailValid) {
                    emailResult.textContent = '사용 가능한 이메일입니다.';
                    emailResult.style.color = 'green';
                } else {
                    emailResult.textContent = '이미 사용 중인 이메일입니다.';
                    emailResult.style.color = 'red';
                }
            } catch (error) {
                console.error('이메일 중복 체크 중 오류 발생 : ', error);
                modalUtils.showAlert('이메일 중복 체크 중 오류가 발생했습니다.', () => { window.location.reload() });
            }
        }
    });

    //전체 모달 클릭 이벤트 리스너
    openModalButton.addEventListener('click', () => {
        if(isOAuth2User) {
            modalUtils.showModal(confirmModal);
        } else {
            modalUtils.showModal(modal)
        }
    }); //회원 탈퇴 버튼 클릭 시 modal -> flex
    modal.querySelector('.cancel').addEventListener('click', () => modalUtils.hideModal(modal)); //돌아가기 버튼 클릭 시 modal -> none
    modal.querySelector('.withdraw').addEventListener('click', () => modalUtils.showModal(confirmModal)); //탈퇴하기 버튼 클릭 시 confirmModal -> flex
    confirmModal.querySelector('.cancel').addEventListener('click', () => modalUtils.hideModal(confirmModal)); //취소 버튼 클릭 시 confirmModal -> none
    confirmModal.querySelector('.confirm').addEventListener('click', deleteUser); //확인 버튼 클릭 시 회원 탈퇴 로직 처리
});