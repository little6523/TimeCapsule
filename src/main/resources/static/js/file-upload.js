const uploadContainer = document.querySelector("#upload-container");
const fileInput = document.getElementById('file-upload');
const uploadText = document.getElementById('upload-text');

// 드래그한 파일 객체가 해당 영역에 놓였을 때
uploadContainer.ondrop = (e) => {
    e.preventDefault();

    // 새로운 파일
    const newFile = e.dataTransfer?.files[0];

    // 파일 검증
    if (!validation(newFile)) {
        uploadContainer.classList.remove("active");
        return;
    }

    // DataTransfer 객체를 이용해 fileInput에 새로운 FileList 생성 (기존 파일을 대체)
    const dataTransfer = new DataTransfer();
    dataTransfer.items.add(newFile);
    fileInput.files = dataTransfer.files;

    // 업로드된 파일 이름을 표시
    renderFile(newFile);
    uploadContainer.classList.remove("active");
}

// ondragover 이벤트가 없으면 onDrop 이벤트가 실행되지 않습니다.
uploadContainer.ondragover = (e) => {
    e.preventDefault();
    uploadContainer.classList.add("active");
}

// 드래그한 파일이 영역을 벗어났을 때
uploadContainer.ondragleave = (e) => {
    e.preventDefault();
    uploadContainer.classList.remove("active");
}

// 첨부 파일 업로드 검증 메소드
function validation(file) {
    const soundFileTypes = ['audio/mpeg', 'audio/wav', 'audio/ogg', 'audio/flac', 'audio/aac', 'audio/m4a', 'audio/x-ms-wma'];

    if (file.name.length > 100) {
        alert("파일명이 100자 이상인 파일은 첨부할 수 없습니다.");
        return false;
    }

    if (file.size > (100 * 1024 * 1024)) {
        alert("최대 파일 용량인 100MB를 초과한 파일은 첨부할 수 없습니다.");
        return false;
    }

    if (file.name.lastIndexOf('.') === -1) {
        alert("확장자가 없는 파일은 첨부할 수 없습니다.");
        return false;
    }

    if (!soundFileTypes.includes(file.type)) {
        alert("사운드 파일만 첨부할 수 있습니다.");
        return false;
    }

    return true;
}

// 파일을 UI에 표시하고 삭제 버튼 추가
function renderFile(file) {
    // 기존 목록 초기화
    uploadText.innerHTML = '';

    if (!file) {
        // 파일이 없을 때 표시할 메시지
        const message = document.createElement('p');
        message.innerHTML = "당신의 소중한 이야기와 추억을 여기에 담아주세요." +
            "<br> (인터뷰 파일 필수, 1개의 파일만 업로드 가능)";
        message.style.whiteSpace = "pre-line"; // 줄바꿈이 적용되도록 설정
        uploadText.appendChild(message);
    } else {
        // 파일 이름을 UI에 표시
        const fileDiv = document.createElement('div');
        fileDiv.className = 'file-item';

        const removeButton = document.createElement('button');
        removeButton.className = 'remove-button';
        removeButton.addEventListener('click', () => {
            removeFile();
        });

        const blank = document.createElement('span');
        blank.textContent = ' ';

        const fileName = document.createElement('span');
        fileName.textContent = file.name;

        fileDiv.appendChild(removeButton);
        fileDiv.appendChild(blank);
        fileDiv.appendChild(fileName);
        uploadText.appendChild(fileDiv);
    }
}

// 파일을 제거하는 함수
function removeFile() {
    // fileInput의 파일을 초기화
    const dataTransfer = new DataTransfer();
    fileInput.files = dataTransfer.files; // 빈 FileList 생성

    // UI 갱신
    renderFile(null);
}
