/*
data-role="node":
각 노드를 식별하는 속성.

data-role="expander":
노드를 확장하거나 축소하는데 사용되는 확장기.

data-mode="open" 또는 data-mode="close":
확장기의 현재 상태를 나타내는 속성.

data-id:
각 노드를 식별하는 고유한 ID.

data-text:
노드에 표시되는 텍스트 내용을 지정하는 속성.

data-selected="true" 또는 data-selected="false":
노드가 선택되었는지의 상태를 나타내는 속성.

data-checked="true" 또는 data-checked="false":
노드의 체크 상태를 나타내는 속성.

data-icon:
노드에 표시되는 아이콘을 지정하는 속성.

data-child-loaded="true" 또는 data-child-loaded="false":
노드의 자식 노드가 로드되었는지의 상태를 나타내는 속성.

data-parent:
현재 노드의 부모 노드를 식별하는 속성.

data-has-children="true" 또는 data-has-children="false":
노드가 자식 노드를 가지고 있는지의 상태를 나타내는 속성.

data-expanded="true" 또는 data-expanded="false":
노드가 초기에 확장된 상태인지를 나타내는 속성.

data-draggable="true" 또는 data-draggable="false":
노드를 드래그 가능하게 할지 여부를 나타내는 속성.

data-droppable="true" 또는 data-droppable="false":
노드를 드롭 가능하게 할지 여부를 나타내는 속성.

data-disabled="true" 또는 data-disabled="false":
노드가 비활성화된 상태인지를 나타내는 속성.

data-additional-data:
노드와 관련된 추가 데이터를 저장하는 데 사용되는 속성.

data-template:
노드에 대한 사용자 지정 HTML 템플릿을 정의하는 속성.

data-children:
노드의 자식 노드를 나타내는 배열이나 객체를 저장하는 속성.

data-user-data:
사용자 정의 데이터를 노드에 연결하는 데 사용되는 속성.

data-href:
노드가 클릭되었을 때 이동할 URL을 지정하는 속성.

data-image:
노드에 사용될 이미지의 경로를 지정하는 속성.

data-image-expander-collapsed:
닫힌 상태의 확장기(expander)에 사용될 이미지의 경로를 지정하는 속성.

data-image-expander-expanded:
열린 상태의 확장기에 사용될 이미지의 경로를 지정하는 속성.

data-image-checkbox-unchecked:
체크박스가 해제된 상태에 사용될 이미지의 경로를 지정하는 속성.

data-image-checkbox-checked:
체크박스가 선택된 상태에 사용될 이미지의 경로를 지정하는 속성.

data-image-checkbox-indeterminate:
체크박스가 중간 상태에 있는 경우 사용될 이미지의 경로를 지정하는 속성.

data-image-drag-helper:
드래그 중에 나타나는 도움말 이미지의 경로를 지정하는 속성.

data-hovered:
마우스가 노드 위에 올려진 상태를 나타내는 속성.

data-selected:
노드가 선택된 상태인지 나타내는 속성.

data-checked:
노드에 체크박스가 있는 경우, 체크된 상태를 나타내는 속성.

 */

//마우스 우클릭으로 여는 메뉴
var contextMenuConfig = {
    selector: '[data-role="node"]',
    items: {
        item1: {
            name: '파일 생성',
            icon: 'fa-solid fa-file',
            callback: function (key, options) {
                console.log("item1 클릭됨");
                console.log("key", key);
                console.log("options", options);
                modal8.style.display = 'block';
                document.getElementById('filename2').focus();
            }
        },
        item2: {
            name: '폴더 생성',
            icon: 'fa-solid fa-folder',
            callback: function (key, options) {
                console.log("item2 클릭됨");
                console.log("key", key);
                console.log("options", options);
                modal5.style.display = 'block';
            }
        },
        item3: {
            name: '이름 변경',
            icon: 'fa-solid fa-pen-to-square',
            callback: function (key, options) {
                var $trigger = $(options.$trigger);
                console.log("트리거값: "+$trigger)
                var filename =  $trigger.attr('data-id')
                var filename2 = $trigger.find('a').text();
                if (filename2 === "untitled") {
                    alert("이 파일의 이름은 변경할 수 없습니다.");
                    return;
                }
                console.log("데이터아이디"+filename)
                console.log(key);
                console.log(options);
                if(filename.includes('_File_')) {
                    openRenameFileModal();
                } else {
                    openRenameFolderModal();
                }// 모달 열기
            }
        },
        item4:{
            name: '다운로드',
            icon : 'fa-solid fa-file-arrow-down',
            visible: function (key, options) {
                var $trigger = options.$trigger;
                var filename = $trigger.find('a').attr('href');
                return filename.endsWith('.html') || filename.endsWith('.txt') || filename.endsWith('.css') || filename.endsWith('.js');
            },
            callback : function (key, options) {
                console.log("key", key);
                console.log("options", options);
                var $trigger = options.$trigger;
                var filename = $trigger.find('a').attr('href')
                // span 안의 a 태그의 텍스트를 가져옴
                console.log("Clicked on " + key + " for element with filename: " + filename);
                var filename2 = filename.split(/[\\/]/).pop().replace(/\[^]+$/, '');
                console.log(filename2);
                axios.post("/editor/fileDownload", {filename: filename}, {responseType: 'blob'})
                    .then(response => {
                        const url = window.URL.createObjectURL(new Blob([response.data]));
                        const link = document.createElement('a');
                        link.href = url;
                        link.setAttribute('download', filename2);
                        document.body.appendChild(link);
                        link.click();
                    })
                    .catch(error => {
                        console.error('다운로드 에러:', error);
                    });
            }
        },
        item5: {
            name: 'zip 다운로드',
            icon: 'fa-solid fa-file-zipper',
            visible: function (key, options) {
                var $trigger = options.$trigger;
                var filename = $trigger.find('a').attr('href');
                var dataId = $trigger.attr('data-id');

                // data-id에 '_File_'이 없고, 확장자가 특정한 파일들이 아닌 경우에만 보이도록 설정
                return !dataId.includes('_File_') && (!filename.endsWith('.html') || !filename.endsWith('.txt') || !filename.endsWith('.css') || !filename.endsWith('.js'));
            },
            callback: function (key, options) {
                console.log("key", key);
                console.log("options", options);
                var $trigger = options.$trigger;
                var filename = $trigger.find('a').attr('href');
                // span 안의 a 태그의 텍스트를 가져옴
                console.log("Clicked on " + key + " for element with filename: " + filename);
                var filename2 = filename.split(/[\\/]/).pop().replace(/\.[^.]+$/, '');
                console.log(filename2);
                axios.post("/editor/zipDownload", {filename: filename}, {responseType: 'blob'})
                    .then(response => {
                        const url = window.URL.createObjectURL(new Blob([response.data]));
                        const link = document.createElement('a');
                        link.href = url;
                        link.setAttribute('download', filename2+'.zip');
                        document.body.appendChild(link);
                        link.click();
                    })
                    .catch(error => {
                        console.error('다운로드 에러:', error);
                    });
            }
        },
        item6: {
            name: '삭제',
            icon:'fa-solid fa-trash',
            callback: function (key, options) {
                var $trigger = options.$trigger;
                var filename = $trigger.find('a').attr('href');
                var filename2 = $trigger.find('a').text();
                if (filename2 === "untitled") {
                    alert("이 파일은 삭제할 수 없습니다.");
                    return;
                }
                var confirmed = confirm("정말로 삭제하시겠습니까? 파일명: " + filename2);
                if (confirmed) {
                    console.log("Confirmed deletion for element with filename: " + filename);
                    // 메뉴 아이템을 클릭한 경우의 동작
                    console.log("key", key);
                    console.log("options", options);
                    // span 안의 a 태그의 텍스트를 가져옴
                    console.log("Clicked on " + key + " for element with filename: " + filename);

                    axios.post("/editor/delete", {filename: filename})
                        .then((response) => {
                            showFileList();
                            console.log("삭제됨");
                        })
                        .catch((error) => {
                            console.log(error);
                        });
                }else {
                    showFileList();
                }}
        }
    }
};

function callContextMenu(selector, items) {
    // 직접 contextMenu 호출이 아니라 items만 전달
    $.contextMenu({
        selector: '[data-role="node"]',
        items: {
            item1: {
                name: '파일 생성',
                icon: 'fa-solid fa-file',
                callback: function (key, options) {
                    console.log("item1 클릭됨");
                    console.log("key", key);
                    console.log("options", options);
                    modal8.style.display = 'block';
                    document.getElementById('filename2').focus();
                }
            },
            item2: {
                name: '폴더 생성',
                icon: 'fa-solid fa-folder',
                callback: function (key, options) {
                    console.log("item2 클릭됨");
                    console.log("key", key);
                    console.log("options", options);
                    modal5.style.display = 'block';
                }
            },
            item3: {
                name: '이름 변경',
                icon: 'fa-solid fa-pen-to-square',
                callback: function (key, options) {
                    var $trigger = $(options.$trigger);
                    console.log("트리거값: "+$trigger)
                    var filename =  $trigger.attr('data-id')
                    var filename2 = $trigger.find('a').text();
                    if (filename2 === "untitled") {
                        alert("이 파일의 이름은 변경할 수 없습니다.");
                        return;
                    }
                    console.log("데이터아이디"+filename)
                    console.log(key);
                    console.log(options);
                    if(filename.includes('_File_')) {
                        openRenameFileModal();
                    } else {
                        openRenameFolderModal();
                    }// 모달 열기
                }
            },
            item4:{
                name: '다운로드',
                icon : 'fa-solid fa-file-arrow-down',
                visible: function (key, options) {
                    var $trigger = options.$trigger;
                    var filename = $trigger.find('a').attr('href');
                    return filename.endsWith('.html') || filename.endsWith('.txt') || filename.endsWith('.css') || filename.endsWith('.js');
                },
                callback : function (key, options) {
                    console.log("key", key);
                    console.log("options", options);
                    var $trigger = options.$trigger;
                    var filename = $trigger.find('a').attr('href')
                    // span 안의 a 태그의 텍스트를 가져옴
                    console.log("Clicked on " + key + " for element with filename: " + filename);
                    var filename2 = filename.split(/[\\/]/).pop().replace(/\[^]+$/, '');
                    console.log(filename2);
                    axios.post("/editor/fileDownload", {filename: filename}, {responseType: 'blob'})
                        .then(response => {
                            const url = window.URL.createObjectURL(new Blob([response.data]));
                            const link = document.createElement('a');
                            link.href = url;
                            link.setAttribute('download', filename2);
                            document.body.appendChild(link);
                            link.click();
                        })
                        .catch(error => {
                            console.error('다운로드 에러:', error);
                        });
                }
            },
            item5: {
                name: 'zip 다운로드',
                icon: 'fa-solid fa-file-zipper',
                visible: function (key, options) {
                    var $trigger = options.$trigger;
                    var filename = $trigger.find('a').attr('href');
                    var dataId = $trigger.attr('data-id');

                    // data-id에 '_File_'이 없고, 확장자가 특정한 파일들이 아닌 경우에만 보이도록 설정
                    return !dataId.includes('_File_') && (!filename.endsWith('.html') || !filename.endsWith('.txt') || !filename.endsWith('.css') || !filename.endsWith('.js'));
                },
                callback: function (key, options) {
                    console.log("key", key);
                    console.log("options", options);
                    var $trigger = options.$trigger;
                    var filename = $trigger.find('a').attr('href');
                    // span 안의 a 태그의 텍스트를 가져옴
                    console.log("Clicked on " + key + " for element with filename: " + filename);
                    var filename2 = filename.split(/[\\/]/).pop().replace(/\.[^.]+$/, '');
                    console.log(filename2);
                    axios.post("/editor/zipDownload", {filename: filename}, {responseType: 'blob'})
                        .then(response => {
                            const url = window.URL.createObjectURL(new Blob([response.data]));
                            const link = document.createElement('a');
                            link.href = url;
                            link.setAttribute('download', filename2+'.zip');
                            document.body.appendChild(link);
                            link.click();
                        })
                        .catch(error => {
                            console.error('다운로드 에러:', error);
                        });
                }
            },
            item6: {
                name: '삭제',
                icon:'fa-solid fa-trash',
                callback: function (key, options) {
                    var $trigger = options.$trigger;
                    var filename = $trigger.find('a').attr('href');
                    var filename2 = $trigger.find('a').text();
                    if (filename2 === "untitled") {
                        alert("이 파일은 삭제할 수 없습니다.");
                        return;
                    }
                    var confirmed = confirm("정말로 삭제하시겠습니까? 파일명: " + filename2);
                    if (confirmed) {
                        console.log("Confirmed deletion for element with filename: " + filename);
                        // 메뉴 아이템을 클릭한 경우의 동작
                        console.log("key", key);
                        console.log("options", options);
                        // span 안의 a 태그의 텍스트를 가져옴
                        console.log("Clicked on " + key + " for element with filename: " + filename);

                        axios.post("/editor/delete", {filename: filename})
                            .then((response) => {
                                showFileList();
                                console.log("삭제됨");
                            })
                            .catch((error) => {
                                console.log(error);
                            });
                    }else {
                        showFileList();
                    }}
            }
        }
    });
    console.log("Context menu initialized");
}

// 우클릭 이벤트 발생 시 실행되는 코드
$(document).on('contextmenu', contextMenuConfig.selector, function (e) {
    e.preventDefault(); // 기본 컨텍스트 메뉴가 나타나지 않도록 합니다.

    const $target = $(e.target);
    if ($target.is('a')) {
        callContextMenu(contextMenuConfig.selector, contextMenuConfig.items);
    } else {
        // 그 외의 경우 컨텍스트 메뉴 호출
        callContextMenu(contextMenuConfig.selector, contextMenuConfig.items);
    }
});

// 컨텍스트 메뉴를 닫을 때마다 다시 정의
$(document).on('contextmenu:hide', function (e) {
    // 감지된 contextmenu 요소의 selector와 items를 추출
    const selector = contextMenuConfig.selector;
    const items = contextMenuConfig.items;

    // 이벤트를 통해 selector와 items를 전달하여 다시 정의
    callContextMenu(selector, items);

    // 추가: contextMenu 초기화
    contextMenuInitialized = false;
});
var $nodes = $('[data-role="node"]');
console.log($nodes.length + " nodes found");
$(document).on('contextmenu', function (e) {
    console.log("Document context menu triggered");
});
/////현재파일의 값이 빈칸이면 다른이름으로 저장 버튼 안보이게 설정
var downloadName = document.getElementById('downloadName');
var changeNameLi = document.getElementById('changeName');

function updateChangeNameVisibility() {
    if (downloadName.value) {
        changeNameLi.style.display = 'list-item';  // 보임
    } else {
        changeNameLi.style.display = 'none';  // 숨김
    }
}
updateChangeNameVisibility(); // 페이지 로드 시 실행
/////////////////////////////////////// 모달창 및 사이드메뉴 ////////////////////////////////////////
const loginCheck = document.getElementById('loginCheck').value;

const icon = document.getElementById('icon'); //헤더 버튼
const balloon = document.getElementById('balloon');

const modal5 = document.getElementById('modalWrap5'); //폴더생성
const closeBtn5 = document.getElementById('closeBtn5'); //폴더생성

const modal6 = document.getElementById('modalWrap6'); //다른이름으로저장
const closeBtn6 = document.getElementById('closeBtn6'); //다른이름으로저장


const btn = document.getElementById('popupBtn'); //저장 버튼
const modal = document.getElementById('modalWrap'); //저장 모달창
const closeBtn = document.getElementById('closeBtn');//저장모달창 끄는 버튼

const modal2 = document.getElementById('renameFileModal'); //이름변경 모달창
const btn2 = document.getElementById('closeBtn2'); //이름변경 모달창

const modal8 = document.getElementById('modalWrap2'); //파일생성모달
const closeBtn8 = document.getElementById('closeBtn8');

const modal7 = document.getElementById('renameFolderModal'); //폴더이름변경 모달창
const btn7 = document.getElementById('closeBtn7'); //폴더이름변경 모달창

const btn3 = document.getElementById('popupBtn2'); //저장 옆 더보기 버튼
const modal3 = document.getElementById('moreNav'); //저장 옆 더보기 모달창

const btn4 = document.getElementById('popupBtn3'); //저장소 버튼
const modal4 = document.getElementById('moreNav2'); //저장소 모달창

icon.addEventListener('click', function (){
    if(balloon.style.display === 'none') {
        balloon.style.display = 'block';
    }else if(balloon.style.display === 'block' ) {
        balloon.style.display = 'none';
    }
});

closeBtn6.onclick = function() {
    document.getElementById('filename3').value = '';
    modal6.style.display = 'none';
}

closeBtn5.onclick = function() {
    document.getElementById('mkdirname').value = '';
    modal5.style.display = 'none';
}

closeBtn.onclick = function() {
    document.getElementById('filename').value = '';
    modal.style.display = 'none';
}

closeBtn8.onclick = function() {
    document.getElementById('filename2').value = '';
    modal8.style.display = 'none';
}

btn3.onclick = function() {
    if(loginCheck == 'true') {
        modal3.style.display = 'block';
    } else if(loginCheck == 'false') {
        alert("로그인 후 사용 가능합니다:)");
    }
}

btn4.onclick = function() {
    if(loginCheck == 'true') {
        modal4.style.display = 'block';
    } else if(loginCheck == 'false') {
        alert("로그인 후 사용 가능합니다:)");
    }

}

window.onclick = function(event) {

    if (event.target === modal || event.target === modal2 || event.target === modal3 || event.target === modal4 ||event.target === modal5 || event.target === modal6 || event.target === modal7 ) {
        modal.style.display = "none";
        modal2.style.display = "none";
        modal3.style.display = "none";
        modal4.style.display = "none";
        modal5.style.display = "none";
        modal6.style.display = "none";
        modal7.style.display = "none";
        modal8.style.display = "none";
        document.getElementById('filename').value = ''; //저장 모달
        document.getElementById('filename2').value = ''; //파일생성 모달
        document.getElementById('newFilename').value = ''; //이름변경 모달
        document.getElementById('mkdirname').value = ''; //폴더생성
        document.getElementById('filename3').value = ''; // 다른이름으로 저장
        document.getElementById('newFoldername').value = ''; //폴더이름변경
    }
}


//////////////////////////////////// Split //////////////////////////////////////
Split(['#top-pane', '#middle-pane', '#bottom-pane'], {
    direction: 'vertical',
    minSize: [30, 30, 140]
});

Split(['#split', '#view'], {
    sizes: [25,75],
    minSize: [230,0]
});


<!--파일 확장자를 선택자로 추가해서 파일 만들기-->
    const extensionSelect = document.getElementById("extension"); //select문에서 선택한 값
    const customExtensionInput = document.getElementById("customExtensionInput"); //직접입력 확장자의 div 값 입력한 값
    const customExtension = document.getElementById("customExtension"); //직접입력 확장자로 입력한 input 값
    const downloadNameInput = document.getElementById("downloadName"); //현재출력중인 파일명

    // select문에서 직접입력을 선택했을때 직접입력 input이 보이고 안보이고 기능
    extensionSelect.addEventListener("change", function () {
    if (extensionSelect.value === "") {
    // "직접입력"을 선택했을 때
    customExtensionInput.style.display = "block";
} else {
    // 다른 옵션을 선택했을 때
    customExtensionInput.style.display = "none";
    downloadNameInput.style.display = "block";
}
});

    // 직접 입력 확장자 input에 값을 입력했을때 직접입력 확장자의 값과 확장자 select문의 값이 같도록함.
    customExtension.addEventListener("input", function () {
    // 직접 입력 값을 select의 값으로 설정
    extensionSelect.value = customExtensionInput.value;
    console.log(extensionSelect.value)
    console.log(customExtension.value)
});

<!--다운로드 버튼 작동하는 스크립트-->

document.getElementById("saveBtn").addEventListener("click", () => {
    let filename = document.getElementById("downloadName").value; // 현재 출력 중인 파일명 값
    if (!filename.trim()) {
        // filename 값이 비어 있으면 "untitled.html"로 설정
        filename = "untitled.html";
    }
    let downname = filename.replace(/\.[^.]+$/, '');
    // 사용자가 입력한 파일명과 조합된 확장자를 설정
    let fullFilename = filename;
    let cssExtension = ".css";
    let jsExtension = ".js";
    let htmlExtension = ".html";
    let savedFilename = downname+"_html" + htmlExtension;
    let savedCssname = downname +"_css" +cssExtension;
    let savedJsname = downname+"_js" + jsExtension;

    // 사용자가 입력한 파일명과 조합된 확장자를 설정
    const htmlCode = htmlEditor.getValue(); // html에 입력한 값
    const cssCode = cssEditor.getValue(); // css에 입력한 값
    const jsCode = jsEditor.getValue(); // js에 입력한 값
    const content = `<html>\n<head>\n<style>\n${cssCode}\n</style>\n</head>\n<body>\n${htmlCode}\n</body>\n<script>\n${jsCode}\n<\/script>\n</html>`;
    const cssContent = cssCode;
    const jsContent = jsCode;
    console.log(jsCode)
    const htmlContent = `<html>\n<head>\n<link rel="stylesheet" href="./${savedCssname}" />\n</head>\n<body>\n${htmlCode}\n</body>\n<script src="./${savedJsname}" ><\/script>\n</html>`;

    // Blob 생성
    const blob = new Blob([content], { type: "text/plain" });
    const htmlBlob = new Blob([htmlContent], { type: "text/plain" });
    const jsBlob = new Blob([jsContent], { type: "text/plain" });
    const cssBlob = new Blob([cssContent], { type: "text/plain" });

    // a 태그를 생성하고 클릭 이벤트를 발생시켜 다운로드
    const downloadLink = document.createElement("a");

    // HTML 파일 다운로드
    if(htmlCode.trim() !==''&&filename.endsWith(htmlExtension)) {
        downloadLink.href = window.URL.createObjectURL(htmlBlob);
        downloadLink.download = savedFilename;
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
        downloadLink.href = window.URL.createObjectURL(jsBlob);
        downloadLink.download = savedJsname;
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
        downloadLink.href = window.URL.createObjectURL(cssBlob);
        downloadLink.download = savedCssname;
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);

    }
    // JavaScript 파일 다운로드
    if (filename.endsWith(jsExtension) && jsCode.trim() !== '') {
        const jsBlob = new Blob([jsCode], { type: "text/plain" });
        downloadLink.href = window.URL.createObjectURL(jsBlob);
        downloadLink.download = downname+jsExtension;
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
    }

    // CSS 파일 다운로드
    if (filename.endsWith(cssExtension) && cssCode.trim() !== '') {
        const cssBlob = new Blob([cssCode], { type: "text/plain" });
        downloadLink.href = window.URL.createObjectURL(cssBlob);
        downloadLink.download = downname+cssExtension;
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
    }
    if (filename.endsWith(htmlExtension)){
        // 전체 파일 다운로드
        downloadLink.href = window.URL.createObjectURL(blob);
        downloadLink.download = fullFilename;
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
    }
});


<!--파일이름에 사용할수 없는 문자들-->
    function isValidFilename(filename) {
    // 사용할 수 없는 문자와 규칙을 정의합니다.
    const invalidChars = /[\/:*?"<>|.]/;
    const invalidNames = ['con', 'prn', 'aux', 'nul', 'com1', 'com2', 'com3', 'com4', 'com5', 'com6', 'com7', 'com8', 'com9', 'lpt1', 'lpt2', 'lpt3', 'lpt4', 'lpt5', 'lpt6', 'lpt7', 'lpt8', 'lpt9'];

    // 사용자가 입력한 파일 이름을 확인합니다.
    if (!filename || invalidChars.test(filename) || invalidNames.includes(filename.toLowerCase())) {
    return false;
}

    return true;
}

<!--파일 이름 변경시 사용할수 없는 문자-->
    function isValidFilename2(newfilename) {
    // 사용할 수 없는 문자와 규칙을 정의합니다.
    const invalidChars = /[\/:*?"<>|.]/;
    const invalidNames = ['con', 'prn', 'aux', 'nul', 'com1', 'com2', 'com3', 'com4', 'com5', 'com6', 'com7', 'com8', 'com9', 'lpt1', 'lpt2', 'lpt3', 'lpt4', 'lpt5', 'lpt6', 'lpt7', 'lpt8', 'lpt9'];

    // 사용자가 입력한 파일 이름을 확인합니다.
    if (!newfilename || invalidChars.test(newfilename) || invalidNames.includes(newfilename.toLowerCase())) {
    return false;
}

    return true;
}

<!--폴더 이름 변경시 사용할수 없는 문자-->
function isValidFoldername(newFoldername) {
    // 사용할 수 없는 문자와 규칙을 정의합니다.
    const invalidChars = /[\/:*?"<>|.]/;
    const invalidNames = ['con', 'prn', 'aux', 'nul', 'com1', 'com2', 'com3', 'com4', 'com5', 'com6', 'com7', 'com8', 'com9', 'lpt1', 'lpt2', 'lpt3', 'lpt4', 'lpt5', 'lpt6', 'lpt7', 'lpt8', 'lpt9'];

    // 사용자가 입력한 폴더 이름을 확인합니다.
    if (!newFoldername || invalidChars.test(newFoldername) || invalidNames.includes(newFoldername.toLowerCase())) {
        return false;
    }

    return true;
}


    document.getElementById("changeName").addEventListener("click",function (){
        modal6.style.display="block";
    })

    // 현재파일 untitled일때 저장시키는 기능
document.getElementById("save").addEventListener("click", function () {
    let extension = document.getElementById("extension").value;
    let name =document.getElementById("filename").value; // 저장하고 싶은 파일의 파일명 값
    let filename = name + extension ;

    const htmlCode = htmlEditor.getValue();
    const cssCode = cssEditor.getValue();
    const jsCode = jsEditor.getValue();
    const content = `<html>\n<head>\n<style>\n${cssCode}\n</style>\n</head>\n<body>\n${htmlCode}\n</body>\n<script>\n${jsCode}\n<\/script>\n</html>`;

    if (!isValidFilename(name)) {
        alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
        return; // 추가 실행 중단
    }
    //code라는 변수 선언, 이 변수에 객체 할당(중괄호로 객체생성함.). 객체의 각 속성은 filename, content.

    let Code = {'filename' : filename,'content' : content };
    axios.post("/editor/newsave", Code)

        .then((response) => {
            saveTreeState();
            $('#tree').remove();
            loadFileList();
            alert("파일이 성공적으로 저장되었습니다");
            closeModal();
        })
        .catch((error) => {
            console.error("에러 응답:", error.response); // 에러 응답 자세히 보기

            if (error.response && error.response.data) {
                if (error.response.data.msg) {
                    alert(error.response.data.msg);
                } else {
                    alert(JSON.stringify(error.response.data, null, 2)); // 에러 응답 자세히 보기
                }
            } else {
                alert("에러 발생! 자세한 내용은 콘솔을 확인해주세요.");
                console.error("에러:", error.message);
            }
        });
});

// 다른이름으로 저장
document.getElementById("resave").addEventListener("click", function () {
    let extension = document.getElementById("extension6").value;
    let name =document.getElementById("filename3").value; // 저장하고 싶은 파일의 파일명 값
    let filename = name + extension ;
    folderAndfile = '';
    console.log("저장이됐나요??안되야되는데??: "+folderAndfile)
    const htmlCode = htmlEditor.getValue();
    const cssCode = cssEditor.getValue();
    const jsCode = jsEditor.getValue();
    const content = `<html>\n<head>\n<style>\n${cssCode}\n</style>\n</head>\n<body>\n${htmlCode}\n</body>\n<script>\n${jsCode}\n<\/script>\n</html>`;

    if (!isValidFilename(name)) {
        alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
        return; // 추가 실행 중단
    }
    //code라는 변수 선언, 이 변수에 객체 할당(중괄호로 객체생성함.). 객체의 각 속성은 filename, content.
    let Code = {'filename' : filename,'content' : content };
    axios.post("/editor/renamesave", Code)
        .then((response) => {
            document.getElementById('filename3').value = '';
            saveTreeState();
            $('#tree').remove();
            loadFileList();
            alert("파일이 성공적으로 저장되었습니다");
            closeModal6();
        })
        .catch((error) => {
            console.error("에러 응답:", error.response); // 에러 응답 자세히 보기

            if (error.response && error.response.data) {
                if (error.response.data.msg) {
                    alert(error.response.data.msg);
                } else {
                    alert(JSON.stringify(error.response.data, null, 2)); // 에러 응답 자세히 보기
                }
            } else {
                alert("에러 발생! 자세한 내용은 콘솔을 확인해주세요.");
                console.error("에러:", error.message);
            }
        });
});
var folderAndfile = '';
<!--불러오기 버튼-->
// DOMContentLoaded는 문서의 초기 html 구조가 완전히 로드되고 파싱되었을때 발생하는 이벤트-
// 스타일시트(style태그), 하위프레임(iframe)등의 외부 리소스가 아직 로드되지 않아도 발생.
    document.addEventListener('DOMContentLoaded', function() {
    // 초기 파일 목록 불러오기
    loadFileList();
});

<!--저장소 모달창 작동 스크립트-->

    // "불러오기" 버튼 클릭 시 모달 열기
    function showFileList() {
    saveTreeState();
    $('#tree').remove();
    loadFileList();
    // 파일 목록을 불러오는 함수 호출
    // 모달 열기
    openModal();
}

    //저장소 모달 열기 함수
    function openModal() {
    document.getElementById('balloon3').style.display = 'block';

}

    //저장소 모달 닫기 함수
    function closeModal() {
        document.getElementById('filename').value = '';
        modal.style.display = 'none';

}
function closeModal6() {
    document.getElementById('filename3').value = '';
    modal6.style.display = 'none';
}

<!--저장소에서 폴더 열림 닫힘 상태 저장하는 스크립트-->
//node는 트리구조에서의 기본단위다. 각 노드는 특정한 데이터를 나타내고 이 데이터 간에 부모-자식 관계가 있따.
//트리에서 각각의 데이터를 담고 있는 각 요소를 노드라고 부른다.
//트리의 최상위 노드를 루트 노드(Root Node)라고 한다.

    let savedState = [];
    function saveTreeState() {
    savedState = [];
    $('#tree').find('[data-role="node"]').each(function() {
    let expander = $(this).find('[data-role="expander"]');//트리에서 노드를 확장하거나 축소하는 확장기(expander)를 식별하는데 활용
    if (expander.attr('data-mode') === 'open') {
    savedState.push($(this).attr('data-id')); // 열린 노드의 id를 savedState배열에 추가
}
});
}

    // data-role="node"는 각 노드를 식별하기위함. 특정 html요소가 트리에서 노드로 간주되도록 지정.
    function restoreTreeState() {
    $('#tree').find('[data-role="node"]').each(function() {
        let node = $(this); //현재 순회중인 노드를 jquery 객체로 저장
        let id = node.attr('data-id'); // 현재 노드의 id를 가져와서 변수 id에 저장

        if (savedState.includes(id)) { //savedstate에 현재 노드의 id가 포함되어 있는지 확인
            // 노드가 열려 있을 경우, 해당 노드의 확장기(expander)의 data-mode 속성을 'open'으로 설정하고, 아이콘의 클래스를 변경하여 열린 상태를 시각적으로 나타낸다
            node.find('[data-role="expander"]').attr('data-mode', 'open').find('i.gj-icon').removeClass('chevron-right').addClass('chevron-down');
            //노드가 열려 있을경우 하위 ul요소를 보이도록 설정
            node.children('ul').show();
        } else {
            node.find('[data-role="expander"]').attr('data-mode', 'close').find('i.gj-icon').removeClass('chevron-down').addClass('chevron-right');
            node.children('ul').hide();
        }
    });
        // 드래그 기능 막음
        document.addEventListener('dragstart', function(event) {
            event.preventDefault();
        });
}

//드래그 우클릭방지에 필요
var isLeftMouseDown = false;
// 페이지 로드 시에 실행되도록
function loadFileList() {
    axios.get('/editor/fileList').then(response => {
        console.log("실행됨!");
        const fileList = response.data;
        console.log("결과값:" + fileList);

        // 이미 트리가 있는지 여부 확인하고 없다면 새로운 div요소를 생성하여 해당 요소에 트리뷰 추가
        if ($('#pane').find('#tree').length === 0) {
            $('#pane').append('<div id="tree"></div>');
        }

        // 새로운 파일 목록으로 트리뷰 재구성
        var tree = $('#tree').tree({
            primaryKey: 'id',
            uiLibrary: 'materialdesign',
            dataSource: transformToTreeViewFormat(fileList),
            imageUrlField: 'flagUrl',
            dragAndDrop: true // 드래그 앤 드롭 활성화
        });

        tree.on('contextmenu', function(e) {
            e.preventDefault();
        });

        tree.on('mousedown', function(e) {
            if (e.which !== 1) {
                e.preventDefault();
            }
        });
        tree.on('dragstart', function(e) {
            e.preventDefault();
        });


        function mouseMoveHandler(e) {
            // 좌클릭인 경우에만 mousemove 이벤트 처리
            if (isLeftMouseDown) {
                // 여기에서 원하는 동작을 수행

                // 좌클릭 드래그 이벤트 처리
                // 예: 드래그 중 로직 추가
            }
        }

        tree.on('mouseup', function(e) {
            // 여기에서 원하는 동작을 수행

            // 드래그 종료 로직 추가

            // mousemove 이벤트 리스너 해제
            tree.off('mousemove', mouseMoveHandler);

            // 마우스 상태 초기화
            isLeftMouseDown = false;
        });


        tree.on('nodeDrop', function (e, id, parentId, orderNumber) {
            var data = tree.getDataById(id),
                parent = parentId ? tree.getDataById(parentId) : {};
            // JSON 문자열에서 직접 값을 추출
            var dragFileHref = data.text.match(/href='([^']*)'/)[1];
            console.log("dragFileHref: " + dragFileHref)
            if(dragFileHref === "\\untitled"){
                console.log("dragFileHref: " + dragFileHref)
                alert("이 파일의 위치를 변경시킬 수 없습니다.");
                e.stopPropagation();  // 이벤트 전파 중단
                return false;
            }
            var dragFolderHref = parent.text.match(/href='([^']*)'/)[1];

// 마지막 '\'의 인덱스를 찾음
            var lastBackslashIndex = dragFolderHref.lastIndexOf('\\');

// '\' 다음의 문자열을 추출
            var lastPart = dragFolderHref.substring(lastBackslashIndex + 1);

// 추출한 문자열 중에 '.'이 포함되어 있다면 '.' 이전까지의 부분만 유지
            if (lastPart.includes('.')) {
                dragFolderHref  = dragFolderHref.substring(0, lastBackslashIndex)+'\\';

                console.log("자름: "+dragFolderHref);
            } else {
                console.log("안자름: "+dragFolderHref);  // '.'이 없으면 원래 문자열 유지
            }

            console.log("폴더위치: "+dragFolderHref);
            // 추가: 파일인 경우에만 이동 요청을 서버로 보냄
                let DragFile = { 'filehref': dragFileHref, 'folderhref': dragFolderHref };
                axios.post("/editor/drag", DragFile)
                    .then((response) => {
                        saveTreeState();
                        $('#tree').remove();
                        loadFileList();
                    })
                    .catch((error) => {
                       showFileList();
                        console.error("에러응답:", error.response);
                    });
        });

        // 트리 재구성 후 상태 복원
        restoreTreeState();
        treeEvent(tree); // 트리 객체를 전달
        updateTreeView();
        // console.log("되나?")
    }).catch(error => {
        console.error('Error fetching file list:', error);
    });
}


function isDropAllowed(data, parent) {
    // 여기에 특정 조건을 추가하여 드롭을 허용할지 여부를 결정
    // 예: 폴더인 경우에만 드롭을 허용하도록 설정
    return parent.flagUrl.includes('folder.svg');
}

    // FileNode 객체를 트리뷰 형식으로 변환
    function transformToTreeViewFormat(fileList) {
        var treeData = [];
        fileList.forEach(function(fileNode) {
            convertNode(fileNode, treeData, 1); // 재귀적으로 노드를 변환합니다. 재귀적- 함수내에서 같은 함수를 호출하는것
        });
        return treeData;
    }

    // FileNode 객체를 트리뷰 노드로 변환
function convertNode(fileNode, treeData, parentId) {
    var nodeId;

    // 이미지의 URL을 통해 파일과 폴더를 구분
    var isFolder = fileNode.flagUrl.includes('folder.svg');

    if (isFolder) {
        // 폴더인 경우: 부모 노드 ID와 자식 노드 이름을 조합하여 유일한 ID 생성
        nodeId = parentId +'_'+"Folder"+'_' + fileNode.name;
    } else {
        // 파일인 경우: 부모 노드 ID와 자식 노드 이름을 조합하여 유일한 ID 생성
        nodeId = parentId+'_'+"File"+'_' + fileNode.name;
    }

    var node = {
        id: nodeId,
        text: "<a href='" + fileNode.text + "'>" + fileNode.name + "</a>",
        flagUrl: fileNode.flagUrl,
        children: []
    };

    fileNode.children.forEach(function (child, index) {
        convertNode(child, node.children, nodeId + '_' + index); // 자식 노드의 ID 생성에 부모 노드 ID를 추가
    });

    treeData.push(node);
}
    //html , css, jscode값을 분배하는 코드
function setCodeValues(htmlCode, cssCode, jsCode) {
    // 코드 미러 에디터에 값 설정
    if (htmlCode.trim() !== '') {
        htmlEditor.setValue(htmlCode);
    } else {
        // 기존 내용이 있으면 이전 내용을 유지합니다.
        htmlEditor.setValue(htmlEditor.getValue());
    }

    if (cssCode.trim() !== '') {
        cssEditor.setValue(cssCode);
    } else {
        cssEditor.setValue(cssEditor.getValue());
    }

    if (jsCode.trim() !== '') {
        jsEditor.setValue(jsCode);
    } else {
        jsEditor.setValue(jsEditor.getValue());
    }
}
function treeEvent() {
    const treeArea = document.querySelector('#tree');

    treeArea.addEventListener('click', function (event) {
        if (event.target.closest('a')) {
            event.preventDefault();
        }
        updateTreeView();
    });

    treeArea.addEventListener('contextmenu', function (event) {
        const anchor2 = event.target.closest('a');
        if (anchor2) {
            const filename = anchor2.textContent.trim();
            handleFileSelection(filename);
            handleFolderSelection(filename);
            rehandleFileSelection(anchor2.getAttribute("href"));
            rehandleFolderSelection(anchor2.getAttribute("href"));
            console.log("파일폴더:" + rehandleFileSelection(anchor2.getAttribute("href")));
        }
        const displayElement = event.target.closest('[data-role="node"]');
        // 이전에 선택된 요소의 클래스와 속성 초기화
        $('.gj-list-md-active').removeClass('gj-list-md-active').removeAttr('data-selected');
        // 선택된 [data-role="display"] 요소에 클래스와 속성 추가
        $(displayElement).addClass('gj-list-md-active');
        $(displayElement).attr('data-selected', 'true');
    });

    treeArea.addEventListener('dblclick', function (event) {
        const anchor = event.target.closest('a');
        if (anchor) {
            const dataId = anchor.closest('[data-role="node"]').getAttribute('data-id');

            if (dataId.includes('_File_')) {
                console.log("파일을 더블클릭했습니다.");

                console.log("폴더파일1:" + anchor);
                folderAndfile = anchor.getAttribute('href');
                console.log("폴더파일2:" + folderAndfile);
                htmlEditor.setValue("");
                cssEditor.setValue("");
                jsEditor.setValue("");
                console.log("folderAndfile: "+folderAndfile)
                const filename = anchor.textContent;
                document.getElementById("downloadName").value = filename;
                document.getElementById("autoname").value = filename;
                updateChangeNameVisibility();
                axios.post('/editor/read', null, {
                    params: {
                        filename2: folderAndfile
                    }
                })
                    .then(response => {
                        const { fileContent, folderAndFile } = response.data;
                        const codeEl = document.querySelector("[data-code]").contentWindow.document;

                        // matchResult가 null이 아니라면 배열로 분해하고, null이라면 각 변수에 빈 문자열 할당
                        const matchResult = fileContent.match(/<style>([\s\S]*?)<\/style>|<body>([\s\S]*?)<\/body>|<script>([\s\S]*?)<\/script>/g);
                        const [styleMatch, bodyMatch, scriptMatch] = matchResult || ['', '', ''];

                        const cssCode = styleMatch && styleMatch.includes('<style>') ? styleMatch.replace(/<style>|<\/style>/g, '').trim() : '';
                        // const cssCode = styleMatch ? styleMatch.replace(/<style>|<\/style>/g, '').trim() : '';
                        console.log(cssCode)
                        const htmlCode = bodyMatch ? bodyMatch.replace(/<body>|<\/body>/g, '').trim() : '';
                        console.log(htmlCode)
                        const jsCode = scriptMatch && scriptMatch.includes('<script>') ? scriptMatch.replace(/<script>|<\/script>/g, '').trim() : '';
                        console.log("jsCode: " + jsCode);
                        setCodeValues(htmlCode, cssCode, jsCode);
                        showFileList();
                    })
                    .catch(error => console.error('파일 내용을 불러오는 중 오류 발생:', error))
                    .finally(() => {
                        closeModal();
                    });
            } else {
                console.log("폴더 더블클릭은 무시합니다.");
            }
        }
    });

    treeArea.addEventListener('dblclick', function (event) {
        const anchor = event.target.closest('a');
        if (anchor) {
            const wrapper = anchor.closest('[data-role="wrapper"]');
            const expander = wrapper.querySelector('[data-role="expander"]');
            const childUl = wrapper.parentElement.querySelector('ul');

            if (expander && childUl) {
                const currentMode = expander.getAttribute('data-mode');
                if (currentMode === 'open') {
                    expander.setAttribute('data-mode', 'close');
                    childUl.style.display = 'none';
                } else {
                    expander.setAttribute('data-mode', 'open');
                    childUl.style.display = 'block';
                }
            }
        }
    });
    }

<!--저장버튼 작동하는 스크립트-->
document.getElementById("popupBtn").addEventListener("click",function () {
    if(loginCheck == 'false') {
        alert("로그인 후 사용 가능합니다:)");
        return;
    }

    var filename = document.getElementById("downloadName").value;
    if (filename === "") {
        modal.style.display = 'block';
    } else{
        // let filename = document.getElementById("downloadName").value;
        const htmlCode = htmlEditor.getValue();
        const cssCode = cssEditor.getValue();
        const jsCode = jsEditor.getValue();
        const content = `<html>\n<head>\n<style>\n${cssCode}\n</style>\n</head>\n<body>\n${htmlCode}\n</body>\n<script>\n${jsCode}\n<\/script>\n</html>`;
        // "\\"를 기준으로 폴더 경로를 나누고, 마지막 요소를 제외한 나머지를 합침
        const folderPathArray = folderAndfile.split("\\");
        console.log(folderAndfile);
        folderPathArray.pop(); // 마지막 요소(파일명)를 제외
        const poppedElement = folderPathArray.pop(); // 마지막 요소(폴더명)를 저장
        console.log("팝: " + poppedElement);
        const fileroot = "\\" + poppedElement; // 새로운 폴더 경로
        console.log("저장폴더경로: " + fileroot);
        console.log("filename: "+filename)
        let Code = {'filename': filename, 'content': content , 'filehref':fileroot};
        console.log("코드값: "+Code)
        axios.post("/editor/save", Code)
            .then((response) => {
                document.getElementById('filename').value = ''
                saveTreeState();
                $('#tree').remove();
                loadFileList();
                alert("파일이 성공적으로 저장되었습니다");
            })
            .catch((error) => {
                console.error("에러 응답:", error.response); // 에러 응답 자세히 보기

                if (error.response && error.response.data) {
                    if (error.response.data.msg) {
                        alert(error.response.data.msg);
                    } else {
                        alert(JSON.stringify(error.response.data, null, 2)); // 에러 응답 자세히 보기
                    }
                } else {
                    alert("에러 발생! 자세한 내용은 콘솔을 확인해주세요.");
                    console.error("에러:", error.message);
                }
            })}
});

<!--선택한 파일 이름을 받음-->
    let selectedFile = null;

    function handleFileSelection(filename) {
    selectedFile = filename;
    // 필요에 따라 추가적인 로직을 수행할 수 있습니다.
}

<!--우클릭으로 이름변경시 값을 추출-->
    let reselectedFile = null;

    function rehandleFileSelection(anchor2) {
    if (selectedFile) {
    reselectedFile = anchor2;

    return reselectedFile;
    // 선택한 파일에 대한 추가적인 로직을 수행할 수 있습니다.
}
}

<!--선택한 파일 이름을 받음-->
let selectedFolder = null;

function handleFolderSelection(filename) {
    selectedFolder = filename;
    // 필요에 따라 추가적인 로직을 수행할 수 있습니다.
}

<!--우클릭으로 이름변경시 값을 추출-->
let reselectedFolder = null;

function rehandleFolderSelection(anchor2) {
    if (selectedFolder) {
        reselectedFolder = anchor2;

        return reselectedFolder;
        // 선택한 파일에 대한 추가적인 로직을 수행할 수 있습니다.
    }
}

<!--파일 이름 바꾸는 스크립트-->
    // 모달 열기 함수
    function openRenameFileModal() {

    const selectedFile = getSelectedFile();
        console.log("selectedFile1:"+selectedFile);
    if (selectedFile) {
    document.getElementById('selectedFile').value = selectedFile;
    console.log("selectedFile값:"+selectedFile)
        document.getElementById('renameFileModal').style.display = 'block';
} else {
    alert('파일을 선택해주세요.');
}
}

function openRenameFolderModal() {

    const selectedFile = getSelectedFile();
    console.log("selectedFile1:"+selectedFile);
    if (selectedFile) {
        document.getElementById('selectedFile').value = selectedFile;
        console.log("selectedFile값:"+selectedFile)
        document.getElementById('renameFolderModal').style.display = 'block';
    } else {
        alert('파일을 선택해주세요.');
    }
}

    // 현재 선택된 파일의 이름을 가져오는 함수 (수정이 필요할 수 있음)
    function getSelectedFile() {
    if (selectedFile) {
    return selectedFile;
} else {
    const selectedNode = document.querySelector('#tree [data-role="node"][data-selected="true"]');
    if (selectedNode) {
    return selectedNode.textContent.trim();
} else {
    return null;
}
}
}



    // 모달 닫기 함수
    function closeRenameFileModal() {
        document.getElementById('newFilename').value = '';
    document.getElementById('renameFileModal').style.display = 'none';
}

function closeRenameFolderModal() {
    document.getElementById('newFoldername').value = '';
    document.getElementById('renameFolderModal').style.display = 'none';
}

    // 파일 이름 변경 함수
    function renameFile() {
    const reextensionSelect = document.getElementById("reextension"); // 파일확장자 값
    const currentFilename = document.getElementById("selectedFile").value; // 현재 파일이름명 값
    const newFileSet = document.getElementById("newFilename").value; //새로 입력한 파일이름 값 (파일이름명 규칙 때문에 따로 만듬)
    const newFilename = document.getElementById("newFilename").value+reextensionSelect.value; // 새로입력한 파일이름 + 확장자값
    const currentFileAndFolder = reselectedFile; // 선택한 파일의 href값

    //파일이름명 규칙 실행
    if (!isValidFilename2(newFileSet)) {
    alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
    return; // 추가 실행 중단
}
    // 마지막 역슬래시 이후의 경로 부분 추출
    const lastBackslashIndex = currentFileAndFolder.lastIndexOf("\\");
    //href값에서 뒤의 파일명은 제거한 폴더 경로값만 추출
    const currentFolder = lastBackslashIndex !== -1 ? currentFileAndFolder.substring(0, lastBackslashIndex + 1) : "";


    // 파일을 백엔드에서 이름을 변경하도록 AJAX 요청을 보냅니다.
    axios.post("/editor/rename", null, {
    params: {
    currentFilename: currentFilename,
    newFilename: newFilename,
    currentFolder: currentFolder
}
})
    .then((response) => {
    document.getElementById('newFilename').value = ''
    showFileList();
    closeRenameFileModal();
    alert("파일명 변경 완료");
})
    .catch((error) => {
    console.error("에러 응답:", error.response); // 에러 응답 출력
    if (error.response && error.response.data) {
    alert(error.response.data);
} else {
    console.error("에러:", error.message);
}
});
}

// 폴더 이름 변경
function renameFolder() {
    const currentFoldername = document.getElementById("selectedFolder").value; //현재폴더명
    const newFolderSet = document.getElementById("newFoldername").value; //새로 입력한 파일이름 값 (파일이름명 규칙 때문에 따로 만듬)
    const currentFolder = reselectedFolder; // 선택한 파일의 href값
    const lastBackslashIndex2 = reselectedFolder.lastIndexOf('\\');
    const folder = reselectedFolder.substring(0, lastBackslashIndex2);
    console.log("folder"+folder)
    console.log("newFolderSet: "+newFolderSet);
    console.log("currentFolder: "+currentFolder);
    console.log("newpath: "+folder);

    //파일이름명 규칙 실행
    if (!isValidFoldername(newFolderSet)) {
        alert("폴더명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
        return; // 추가 실행 중단
    }
    // 마지막 역슬래시 이후의 경로 부분 추출
    //href값에서 뒤의 파일명은 제거한 폴더 경로값만 추출
    console.log("currentFolder"+currentFolder);

    // 파일을 백엔드에서 이름을 변경하도록 AJAX 요청을 보냅니다.
    axios.post("/editor/renamefolder", null, {
        params: {
            currentFoldername: folder,
            newFoldername: newFolderSet,
            currentFolder: currentFolder
        }
    })
        .then((response) => {
            document.getElementById('newFoldername').value = ''
            showFileList();
            closeRenameFolderModal();
            alert("폴더명 변경 완료");
        })
        .catch((error) => {
            console.error("에러 응답:", error.response); // 에러 응답 출력
            if (error.response && error.response.data) {
                alert(error.response.data);
            } else {
                console.error("에러:", error.message);
            }
        });
}

<!--링크는 새창으로 띄우게함-->

    document.addEventListener('DOMContentLoaded', function () {
    // Wait for the iframe to load
    document.getElementById('content').addEventListener('load', function () {
        // Access the contentDocument of the iframe
        const iframeContent = document.getElementById('content').contentDocument;
        // Check if contentDocument is available
        if (iframeContent) {
            // Find all 'a' tags and set target="_blank"
            const anchorTags = iframeContent.querySelectorAll('a');
            anchorTags.forEach(function (anchor) {
                anchor.setAttribute('target', '_blank');
            });
        }
    });
});


<!--자동저장 기능-->

    function autosave () {
    let extension = document.getElementById("extension").value;
    let name =document.getElementById("autoname").value; // 저장하고 싶은 파일의 파일명 값
    let filename = name + extension ;


    const htmlCode2 = htmlEditor.getValue();
    const cssCode2 = cssEditor.getValue();
    const jsCode2 = jsEditor.getValue();
    const content = `<html>\n<head>\n<style>\n${cssCode2}\n</style>\n</head>\n<body>\n${htmlCode2}\n</body>\n<script>\n${jsCode2}\n<\/script>\n</html>`;
    let local = setInterval(saveToLocalStorage, 4000);
    const folderPathArray2 = folderAndfile.split("\\");
    folderPathArray2.pop(); // 마지막 요소(파일명)를 제외
    const fileroot = folderPathArray2.join("\\");

    let code = {'filename' : name,'content' : content, 'filehref':fileroot };
console.log("filename:"+filename);
console.log("content:"+content);
    axios.post("/editor/autoSave", code)
    .then((response) => {
    // 서버 응답이 성공한 경우
    showSuccessMessage();
    clearInterval(local);
    localStorage.removeItem('htmlContent');
    localStorage.removeItem('cssContent');
    localStorage.removeItem('jsContent');
})
    .catch((error) => {
    if (error.response && error.response.data) {
    if (error.response.data.msg) {
    alert(error.response.data.msg);
} else {
    alert(error.response.data);
}
} else {
    console.error("에러:", error.message);
}
});

}
    // 성공 메시지를 표시하고 2초 후에 숨김
    function showSuccessMessage() {
    const checkCircle = document.getElementById('checkCircle');

    // 성공 메시지를 표시
    checkCircle.style.display = 'inline-block';

    // 2초 후에 다시 숨김
    setTimeout(() => {
    checkCircle.style.display = 'none';
}, 2000);
}


<!--로컬 스토리지에 저장하는 스크립트-->

    function saveToLocalStorage() {

    localStorage.setItem('htmlContent', htmlEditor.getValue());
    localStorage.setItem('cssContent', cssEditor.getValue());
    localStorage.setItem('jsContent', jsEditor.getValue());
}

    // Load content from local storage on page load


    htmlCode= localStorage.getItem('htmlContent') || '';
    cssCode= localStorage.getItem('cssContent') || '';
    jsCode= localStorage.getItem('jsContent') || '';



    document.addEventListener('DOMContentLoaded', function() {
    setInterval(saveToLocalStorage,7000);
    // HTML textarea


    const htmlTextarea = document.getElementById('htmlTextarea');
    let savedHtmlContent = localStorage.getItem('htmlContent');

    // CSS textarea

    const cssTextarea = document.getElementById('cssTextarea');
    let savedCssContent = localStorage.getItem('cssContent');

    // JS textarea

    const jsTextarea = document.getElementById('jsTextarea');
    let savedJsContent = localStorage.getItem('jsContent');

    // 코드가 어디에 어떻게 박히느지 몰라서 추가함..

    // 빈 값인 경우에만 이어서 작업 여부 확인
    if (!savedHtmlContent && !savedCssContent && !savedJsContent) {
    return; // 모든 값이 비어있으면 확인 창을 표시하지 않음
}
    // Display a confirmation dialog with an OK and Cancel button
    var userConfirmed = window.confirm("이전에 저장하지 못한 기록이 있습니다:\n\n 이어서 작업하시겠습니까?");

    // If the user clicks OK, populate textareas with saved values
    if (userConfirmed) {
    if (savedHtmlContent !== null) {
    htmlTextarea.value = savedHtmlContent;
    // htmlCodeEl.value=savedHtmlContent;
    htmlEditor.setValue(savedHtmlContent);
}

    if (savedCssContent !== null) {
    // cssCodeEl.value=savedCssContent;
    cssTextarea.value = savedCssContent;
    cssEditor.setValue(savedCssContent);
}

    if (savedJsContent !== null) {
    // jsCodeEl.value = savedJsContent;
    jsTextarea.value=savedJsContent;
    jsEditor.setValue(savedJsContent);
}
} else { // If the user clicks Cancel, clear the values in the textareas
    // htmlCodeEl.value="";
    // cssCodeEl.value="";
    // jsCodeEl.value="";
    htmlEditor.value="";
    cssEditor.value="";
    jsEditor.value="";
    localStorage.removeItem('htmlContent');
    localStorage.removeItem('cssContent');
    localStorage.removeItem('jsContent');
}});

    const checkbox = document.getElementById("autoSaveCheck");
    let auto; // 변수 선언


    checkbox.addEventListener("change", function() {
    if (checkbox.checked) {
    clearInterval(auto);
    console.log("자동저장중");

    auto = setInterval(autosave,4000);


} else {
    clearInterval(auto);
    console.log("자동저장해제");
    setInterval(saveToLocalStorage, 4000);
}
});

<!--파일업로드 관련 스크립트-->

    document.getElementById('readFile').addEventListener('click', function () {
    // 파일 업로드 input 엘리먼트 생성
    var inputFile = document.createElement('input');
    inputFile.type = 'file';
    inputFile.accept = '.txt,.css,.html,.js,.scss';
    inputFile.maxSize = 10485760;
    inputFile.name = 'upfile';

    // 파일이 선택되었을 때의 이벤트 핸들러
    inputFile.addEventListener('change', function () {
    // FormData 객체 생성
    var formData = new FormData();
    // 선택된 파일 추가
    formData.append('file', this.files[0]);

    // 파일 업로드를 위한 Axios 요청
    axios.post('/editor/readFile', formData, {
    headers: {
    'Content-Type': 'multipart/form-data',
}
})
    .then(function (response) {
    // 서버 응답에서 코드 블록 추출
    const { cssCode, htmlCode, jsCode } = extractCodeBlocks(response.data);
    // 추출된 코드 블록을 에디터에 설정
    setCodeValues(htmlCode, cssCode, jsCode);
})
    .catch(function (error) {
    console.error('Error:', error);
});
});

    // 생성한 input 엘리먼트를 숨김 처리
    inputFile.style.display = 'none';

    // body에 input 엘리먼트 추가
    document.body.appendChild(inputFile);

    // input 엘리먼트 클릭 (파일 업로드 다이얼로그를 엽니다.)
    inputFile.click();
});

    function extractCodeBlocks(fileContent) {
    // 줄바꿈 문자열을 모두 표준화하여 \n으로 변환
    fileContent = fileContent.replace(/\r\n/g, '\n').replace(/\r/g, '\n');

    const headRegex = /<head>([\s\S]*?)<\/head>/g;
    const bodyRegex = /<body>([\s\S]*?)<\/body>/g;
    const styleRegex = /<style>([\s\S]*?)<\/style>/g;
    const scriptRegex = /<script[^>]*>([\s\S]*?)<\/script>/g;

    let bodyCode = '';
    let headCode = '';
    let cssCode = '';
    let htmlCode = '';
    let jsCode = '';
    let match;
    // <script> 태그 일치 항목 찾기
    while ((match = scriptRegex.exec(fileContent)) !== null) {
    const scriptContent = match[1].replace(/^\s*\n/gm, ''); // 첫 번째 빈 줄 제거
    jsCode += scriptContent.trim() + '\n';
}

// jsCode에서 선행 및 후행 빈 줄 제거
    jsCode = jsCode.replace(/(^\s*|\s*$)/g, '');

    fileContent = fileContent.replace(/<script[^>]*>[\s\S]*?<\/script>/g, '');
    // <body> 태그 일치 항목 찾기
    while ((match = bodyRegex.exec(fileContent)) !== null) {
    htmlCode += match[1].trim() + '\n';
}
    fileContent = fileContent.replace(/<body>[\s\S]*?<\/body>/g, '');

    // <style> 태그 일치 항목 찾기

    while ((match = styleRegex.exec(fileContent)) !== null) {
    cssCode += match[1].trim() + '\n';
}

    return { cssCode, htmlCode, jsCode };
}

//////////////////////////////////// 폴더 생성 //////////////////////////////////////
// 폴더명 입력 문자만 입력 가능하도록 처리
function validateInput(input) {
    const validChars = /^[가-힣a-zA-Z0-9]+$/; // 한글, 영어, 숫자만 허용하는 정규 표현식

    // 입력된 값이 유효한 문자만 포함하고 있는지 검사합니다.
    if (!validChars.test(input.value)) {
        alert("한글, 영어, 숫자만 입력할 수 있습니다.");
        input.value = input.value.replace(/[^가-힣a-zA-Z0-9]/g, ''); // 유효하지 않은 문자 제거
    }
}
// 선택한 항목이 디렉토리라면 해당하는 디렉토리 하위에 디렉토리를 생성하고, 파일이면 그 파일이있는 경로에 디렉토리를 생성
document.getElementById("mkdir2").addEventListener("click", function() {
    saveTreeState();

    const selectedElement = document.querySelector('[data-selected="true"]');
    console.log("폴더생성엘리멘트:"+selectedElement);


    var anchor;
    var href;
    if (selectedElement) {
        // data-id 값을 추출
        // const dataId = selectedElement.getAttribute('data-id');
        console.log(selectedElement)
        // 선택된 요소 내부의 a 태그
        anchor = selectedElement.querySelector('a');
        console.log("anchor: "+anchor)
        href = anchor ? anchor.getAttribute('href') : null;

        // console.log('Data ID:', dataId);
        console.log('Href:', href);
    } else {
        console.log('선택된 요소가 없습니다.');
    }

    //작성한 폴더명
    let mkdirname = document.getElementById("mkdirname").value;
    console.log("mkdirname"+mkdirname)

    if (!isValidFoldername(mkdirname)) {
        alert("폴더명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
        return; // 추가 실행 중단
    }
    //현재 선택된 경로
    let path = href;
    console.log("폴더추가 경로"+path)
    let dir = { 'mkdirname': mkdirname, 'path': path };
    axios.post("/editor/mkdir", dir).then((response) => {
        document.getElementById('mkdirname').value = '';
        saveTreeState();
        // 저장 후 파일 목록 다시 불러오기
        $('#tree').remove(); // 트리를 완전히 제거합니다.
        loadFileList();
        modal5.style.display = 'none';
        console.log("폴더생성 완료")
    }).catch((error) => {
        console.log(error);
        console.log(error);
        if (error.response && error.response.status === 409) {
            // HTTP 상태 코드가 409인 경우 중복된 폴더명이라면 알림창을 띄움
            alert('중복된 폴더명입니다.');
        }
    });
});


/////////////////////////////////////////////////// 새 파일 생성 /////////////////////////////////////////////////////////////////
// 선택된 경로에 따라 새 파일 생성
document.getElementById("btn1").addEventListener("click", function() {
    saveTreeState();
    let name = document.getElementById("filename2").value;
    let filename = document.getElementById("filename2").value +document.getElementById("extension7").value ;
    // let monaco =  monaco_test.getValue();
    // let memo = { 'filename': filename, 'monaco': monaco };

    if (!isValidFilename(name)) {
        alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
        return; // 추가 실행 중단
    }
    //
    const selectedElement = document.querySelector('[data-selected="true"]');

    var anchor;
    var href;
    if (selectedElement) {
        // data-id 값을 추출
        const dataId = selectedElement.getAttribute('data-id');
        if(dataId.includes("_File_")) {
            // 선택된 요소 내부의 a 태그
            anchor = selectedElement.querySelector('a');
            href = anchor ? anchor.getAttribute('href') : null;
            var sanitizedPath = href.replace(/\\[^\\]+$/, "");
            console.log('Data ID:', dataId);
            console.log('Href:', sanitizedPath);
        }else {
            anchor = selectedElement.querySelector('a');
            var sanitizedPath = anchor ? anchor.getAttribute('href') : null;
        }
    } else {
        console.log('선택된 요소가 없습니다.');

        var mid = document.getElementById("user_mid").value;
        href = '/'+mid
        console.log('mid : '+href);
    }
    console.log("filename2: "+filename)
    //현재 선택된 경로
    let path = href;
    let dir = { 'filename': filename, 'path': sanitizedPath };

    axios.post("/editor/newfile", dir).then((response) => {
        document.getElementById('filename2').value = ''; //파일생성 모달
        // 저장 후 파일 목록 다시 불러오기
        $('#tree').remove(); // 트리를 완전히 제거합니다.
        loadFileList();
        modal8.style.display = 'none';
        console.log(response)

    }).catch((error) => {
        console.log("에러 : "+error.response.data);
        // console.log("에러 : "+error.response.status);
        if (error.response.status === 409) {
            $('#btn1').blur();
            alert("이미 같은 이름으로 생성된 파일이 존재합니다.")
        }
        if (error.response.status === 507) {
            $('#btn1').blur();
            alert("더 이상 파일 및 폴더를 생성할 수 없습니다.\n (최대 30개의 파일 및 폴더 생성 가능)")
        }

    });

});


//////////////////////////////////// 폴더 확장 여부에 따른 아이콘 변경 ////////////////////////////////////
// treeEvent()의 a 태그 이동 막는 부분에서 사용하면 됨
function updateTreeView() {
    var expanders = document.querySelectorAll('[data-role="expander"]');

    expanders.forEach(function(expander) {
        var image = expander.nextElementSibling; // 'image' span은 'expander' span의 바로 다음 요소
        var icon = expander.querySelector('i'); // 'expander' 내부의 'i' 태그를 찾기

        if (expander.getAttribute('data-mode') === 'open') {
            image.innerHTML = '<img src="/static/img/icon/folder_open_FILL0_wght400_GRAD0_opsz24.svg" width="17.9948" height="31.9878">';
        } else if (expander.getAttribute('data-mode') === 'close' && icon && icon.classList.contains('chevron-right')) {
            image.innerHTML = '<img src="/static/img/icon/folder.svg" width="17.9948" height="31.9878">';
        }
    });
}
