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
    uploadText.innerHTML = Array.from(dataTransfer.files).map(v => v.name).join("<br>");
    uploadContainer.classList.remove("active");
}

// ondragover 이벤트가 없으면 onDrop 이벤트가 실행되지 않습니다.
uploadContainer.ondragover = (e) => {
    e.preventDefault();
}

// 드래그한 파일이 최초로 진입했을 때
uploadContainer.ondragenter = (e) => {
    e.preventDefault();

    uploadContainer.classList.add("active");
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
    } else if (obj.size > (100 * 1024 * 1024)) {
        alert("최대 파일 용량인 100MB를 초과한 파일은 첨부할 수 없습니다.");
        return false;
    } else if (obj.name.lastIndexOf('.') === -1) {
        alert("확장자가 없는 파일은 첨부할 수 없습니다.");
        return false;
    } else if (!soundFileTypes.includes(obj.type) && !imageFileTypes.includes(obj.type)) {
        alert("사운드, 이미지 파일외의 파일은 첨부할 수 없습니다.");
        return false;
    } else {
        return true;
    }
}
