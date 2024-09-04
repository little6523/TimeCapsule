const uploadContainer = document.querySelector("#upload-container");
const fileInput = document.getElementById('file-upload');
const uploadText = document.getElementById('upload-text');

// 드래그한 파일 객체가 해당 영역에 놓였을 때
uploadContainer.ondrop = (e) => {
    e.preventDefault();

    // 새로운 파일 리스트
    const newFiles = [...e.dataTransfer?.files];
    console.log(newFiles);

    for (let i = 0; i < newFiles.length; i++) {
        if (!validation(newFiles[i])) {
            uploadContainer.classList.remove("active");
            return;
        }
    }

    // 기존 파일 목록에 새 파일들 추가
    // 업로드된 파일들을 저장할 배열
    const dataTransfer = new DataTransfer();
    let currentFiles = Array.from(fileInput.files);
    currentFiles = [...currentFiles, ...newFiles];

    // DataTransfer 객체를 이용해 fileInput에 새로운 FileList 생성
    currentFiles.forEach(file => dataTransfer.items.add(file));
    fileInput.files = dataTransfer.files;

    // 업로드된 파일 이름을 표시
    renderFileList(Array.from(dataTransfer.files));
    uploadContainer.classList.remove("active");
}

// ondragover 이벤트가 없으면 onDrop 이벤트가 실행되지 않습니다.
uploadContainer.ondragover = (e) => {
    e.preventDefault();

    uploadContainer.classList.add("active");
}

// 드래그한 파일이 최초로 진입했을 때
uploadContainer.ondragenter = (e) => {
    e.preventDefault();

    // uploadContainer.classList.add("active");
}

// 드래그한 파일이 영역을 벗어났을 때
uploadContainer.ondragleave = (e) => {
    e.preventDefault();

    uploadContainer.classList.remove("active");
}

// 첨부 파일 업로드 검증 메소드
function validation(obj) {
    const soundFileTypes = ['audio/mpeg', 'audio/wav', 'audio/ogg', 'audio/flac', 'audio/aac', 'audio/m4a',
        'audio/x-ms-wma', 'application/x-hwp'];
    const imageFileTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/webp', 'image/tiff'];

    if (obj.name.length > 100) {
        alert("파일명이 100자 이상인 파일은 첨부할 수 없습니다.");
        return false;
    }

    if (obj.size > (100 * 1024 * 1024)) {
        alert("최대 파일 용량인 100MB를 초과한 파일은 첨부할 수 없습니다.");
        return false;
    }

    if (obj.name.lastIndexOf('.') === -1) {
        alert("확장자가 없는 파일은 첨부할 수 없습니다.");
        return false;
    }

    if (!soundFileTypes.includes(obj.type) && !imageFileTypes.includes(obj.type)) {
        alert("사운드, 이미지 파일외의 파일은 첨부할 수 없습니다.");
        return false;
    }

    return true;
}

// 파일 목록을 UI에 표시하고 삭제 버튼 추가
function renderFileList(files) {
    // 기존 목록 초기화
    uploadText.innerHTML = '';

    if (files.length === 0) {
        // 파일이 없을 때 표시할 메시지
        const message = document.createElement('p');
        message.textContent = "당신의 소중한 이야기와 추억을 여기에 담아주세요.\n(인터뷰 파일 필수, 사진 파일 선택 사항)";
        message.style.whiteSpace = "pre-line"; // 줄바꿈이 적용되도록 설정
        uploadText.appendChild(message);
    } else {
        // 파일 목록을 순회하며 UI에 파일 표시
        files.forEach((file, index) => {
            const fileDiv = document.createElement('div');
            fileDiv.className = 'file-item';

            const removeButton = document.createElement('button');

            removeButton.className = 'remove-button';
            removeButton.addEventListener('click', () => {
                removeFile(index);
            });

            const blank = document.createElement('span');
            blank.textContent = ' ';

            const fileName = document.createElement('span');
            fileName.textContent = file.name;

            fileDiv.appendChild(removeButton);
            fileDiv.appendChild(blank);
            fileDiv.appendChild(fileName);
            uploadText.appendChild(fileDiv);
        });
    }
}

// 파일을 제거하는 함수
function removeFile(index) {
    const dataTransfer = new DataTransfer();
    let currentFiles = Array.from(fileInput.files);

    // 해당 인덱스 파일을 제거
    currentFiles.splice(index, 1);

    // 남은 파일들을 다시 DataTransfer에 추가
    currentFiles.forEach(file => dataTransfer.items.add(file));
    fileInput.files = dataTransfer.files;

    // UI 갱신
    renderFileList(currentFiles);
}