//////////////////////////////////// Split //////////////////////////////////////
Split(['#left-pane', '#center-pane', '#right-pane'], {
    sizes: [20, 50,30],
    minSize: [0, 200, 0]
});


//////////////////////////////////// 폴더 생성 //////////////////////////////////////
// 폴더명 입력 문자만 입력 가능하도록 처리
    function validateInput(input) {
        const validChars = /^[가-힣a-zA-Z0-9]+$/; // 한글, 영어, 숫자만 허용하는 정규 표현식

        // 입력된 값이 유효한 문자만 포함하고 있는지 검사합니다.
        if (!validChars.test(input.value)) {
            alert("한글, 영어, 숫자만 입력할 수 있습니다.");
            return false;
            // input.value = input.value.replace(/[^가-힣a-zA-Z0-9]/g, ''); // 유효하지 않은 문자 제거
        } else {
            return true;
        }
    }
    // 선택한 항목이 디렉토리라면 해당하는 디렉토리 하위에 디렉토리를 생성하고, 파일이면 그 파일이있는 경로에 디렉토리를 생성
    document.getElementById("mkdir2").addEventListener("click", function() {
        saveTreeState();
        let mkdirnameInput = document.getElementById("mkdirname");

        // validateInput 함수를 사용하여 입력 검증
        if (!validateInput(mkdirnameInput)) {
            this.blur();
            return false;
        }

        const selectedElement = document.querySelector('[data-selected="true"]');

        var anchor;
        var href;
        if (selectedElement) {
            // data-id 값을 추출
            const dataId = selectedElement.getAttribute('data-id');

            // 선택된 요소 내부의 a 태그
            anchor = selectedElement.querySelector('a');
            href = anchor ? anchor.getAttribute('href') : null;

            console.log('Data ID:', dataId);
            console.log('Href:', href);
        } else {
            console.log('선택된 요소가 없습니다.');

            var mid = document.getElementById("user_mid").value;
            href = '/'+mid
            console.log('mid : '+href);
        }

        //작성한 폴더명
        let mkdirname = document.getElementById("mkdirname").value;
        //현재 선택된 경로
        let path = href;
        let dir = { 'mkdirname': mkdirname, 'path': path };
        axios.post("/api/mkdir", dir).then((response) => {
            // 저장 후 파일 목록 다시 불러오기
            $('#tree').remove(); // 트리를 완전히 제거합니다.
            loadFileList();
            modal.style.display = 'none';

        }).catch((error) => {
            console.log(error);
            if (error.response.status === 409) {
                $('#mkdirname').blur();
                alert("이미 같은 이름으로 생성된 폴더가 존재합니다.")
            }
        });
    });

/////////////////////////////////////////////////// 새 파일 생성 /////////////////////////////////////////////////////////////////
    // 선택된 경로에 따라 새 파일 생성
    document.getElementById("btn1").addEventListener("click", function() {
        saveTreeState();
        let filename = document.getElementById("filename").value;
        // let monaco =  monaco_test.getValue();
        // let memo = { 'filename': filename, 'monaco': monaco };
        console.log("새파일생성 "+filename)
        if (!isValidFilename3(filename)) {
            alert('사용할 수 없는 파일명입니다.')
            return false;
        }
        //
        const selectedElement = document.querySelector('[data-selected="true"]');

        var anchor;
        var href;
        if (selectedElement) {
            // data-id 값을 추출
            const dataId = selectedElement.getAttribute('data-id');

            // 선택된 요소 내부의 a 태그
            anchor = selectedElement.querySelector('a');
            href = anchor ? anchor.getAttribute('href') : null;

            console.log('Data ID:', dataId);
            console.log('Href:', href);
        } else {
            console.log('선택된 요소가 없습니다.');

            var mid = document.getElementById("user_mid").value;
            href = '/'+mid
            console.log('mid : '+href);
        }

        //현재 선택된 경로
        let path = href;
        let dir = { 'filename': filename, 'path': path };

        axios.post("/api/test1", dir).then((response) => {
            // 저장 후 파일 목록 다시 불러오기
            $('#tree').remove(); // 트리를 완전히 제거합니다.
            loadFileList();
            modal2.style.display = 'none';
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

//////////////// 새로 생성된 파일 더블클릭 처리 테스트 ////////////////////
    // function linkDoubleClick(createdFile) {
    //     console.log('link createdFile : '+createdFile)
    //     // href 속성이 createdFile과 일치하는 a 태그 찾기
    //     var link = document.querySelector('a[href="' + createdFile + '"]');
    //     console.log('link : ',link)
    //     // 해당 요소가 존재하면 더블클릭 이벤트 발생시키기
    //     if (link) {
    //         var dblClickEvent = new MouseEvent('dblclick', {
    //             bubbles: true,
    //             cancelable: true,
    //             view: window
    //         });
    //         link.dispatchEvent(dblClickEvent);
    //     } else {
    //         console.log('해당 href 값을 가진 a 태그가 없습니다.');
    //     }
    // }

////////////////////////////////////////////// 파일 내용 저장 (saveBtn) //////////////////////////////////////////////////
// 파일 경로 , 코드 내용 전달 받아서 저장
// 파일이 선택되어 있을때만 가능하도록 처리

function saveFile() {

    // 파일 미선택시 처리
    if (document.getElementById('selectedFileName').textContent === '') {
        alert("선택된 파일이 없습니다.");
        $("#saveBtn").blur();
        return false;
    }

    var fileName = document.getElementById('selectedFileName').title // 파일 경로
    var mid = document.getElementById("user_mid").value;

    var filePath = "/"+ fileName;
    var fileContent = monaco_test.getValue();

    console.log("파일 경로 : "+fileName)
    console.log("에디터 내용 : "+monaco_test.getValue())

    $.ajax({
        type: "POST",
        url: "/api/saveFile",
        data: JSON.stringify({ "content": fileContent ,"filename":fileName}),
        contentType: "application/json",
        success: function(response) {
            // alert(response.message + '\n생성일: ' + response.creationTime + '\n수정일: ' + response.lastModifiedTime);
            console.log(response.message);
            console.log(response.creationTime);
            console.log(response.lastModifiedTime);
            $('#lastSaveTime').text('저장 : '+response.lastModifiedTime);
            oriContent = fileContent;
            $("#saveBtn").blur();

            // 버튼 누르면 3초간 비활성화해서 버튼 여러번 계속 누르는 행위 방지
            const oriSaveBtnInnerHTML = document.getElementById('saveBtn').innerHTML
            document.getElementById('saveBtn').disabled = true;
            document.getElementById('saveBtn').innerHTML = '<span class="spinner-border spinner-border-sm" aria-hidden="true" ></span>' +
                '<span class="visually-hidden" role="status">Loading...</span>';
            // 3초 후 버튼을 다시 활성화
            setTimeout(() => {
                document.getElementById('saveBtn').disabled = false;
                document.getElementById('saveBtn').innerHTML = oriSaveBtnInnerHTML
            }, 3000); // 3000밀리초 = 3초

            },
        error: function(error) {
            alert('파일 저장 실패: ' + error.responseText);
        }
    });


}



/////////////////////////////////////// 자바 코드 실행 ////////////////////////////////////////
    function send_compiler() {

        if (document.getElementById('selectedFileName').textContent === '') {
            alert("파일을 선택해 주세요.");
            $("#run").blur();
            return false;
        }
        if (!document.getElementById('selectedFileName').textContent.endsWith(".java")) {
            alert("자바 파일만 실행 가능합니다.")
            return false;
        }


    console.log(monaco_test.getValue())
        var fileName = document.getElementById('selectedFileName').textContent;

        $("#run").blur();
    $("#output").html("<div style='text-align: center; width: 100%'><div class='spinner-border text-secondary' role='status' >\n" +
        // "  <span class='sr-only' style='text-align: center'>Loading...</span>\n" +
        "</div></div>");

    $.ajax({
    type: "POST",
    url: "/compile",
    data: JSON.stringify({ "code": monaco_test.getValue() ,"fileName":fileName}),
    contentType: "application/json",
    success: function(response) {
    $("#output").html(response.output.replace(/\n/g, "<br>"));

    $("#compileTime").html("<i class=\"fa-solid fa-circle\" style=\"color: #1b4134; font-size: 10px; margin-right: 6px;\"></i>"+response.time + ","+response.date );
},
    error: function(error) {
    $("#output").html("Error: " + error.responseText);
}
});
}





/////////////////////////////////////// 지정한 파일명으로 파일 다운로드  ////////////////////////////////////////
    document.getElementById("downloadBtn").addEventListener("click", () => {

    if (document.getElementById('selectedFileName').textContent === '') {
        alert('선택된 파일이 없습니다.')
        return false;
    }
        console.log(document.getElementById('selectedFileName').textContent)

    var viewLinesElements = document.getElementsByClassName("view-line");
    var allTextValues = [];

    for (var i = 0; i < viewLinesElements.length; i++) {
    var viewLinesElement = viewLinesElements[i];
    var textValue = viewLinesElement.textContent;
    allTextValues.push(textValue);
}
    var textToSave = allTextValues.join('\n');

    var blob = new Blob([textToSave], { type: "text/plain" });


    var a = document.createElement("a");
    a.style.display = "none";
    a.href = window.URL.createObjectURL(blob);
    a.download = document.getElementById('selectedFileName').textContent; // 사용자가 원하는 파일명 지정

    document.body.appendChild(a);
    a.click();

    window.URL.revokeObjectURL(a.href);
    document.body.removeChild(a);
});

/////////////////////////////////////// 처음 에디터 생성  ////////////////////////////////////////
    function setEditor(inputValue){
    return {
    value: inputValue,      // 에디터 내용 설정
    language: "java",    // 언어
    fontSize: 16,
    theme: "vs-dark",   // 테마
    lineNumbers: 'on',  // 줄 번호
    glyphMargin: false, // 체크 이미지 넣을 공간이 생김
    vertical: 'auto',
    horizontal: 'auto',
    verticalScrollbarSize: 10,
    horizontalScrollbarSize: 10,
    scrollBeyondLastLine: false, // 에디터상에서 스크롤이 가능하게
    readOnly: false,    // 수정 가능 여부
    automaticLayout: true, // 부모 div 크기에 맞춰서 자동으로 editor 크기 맞춰줌
    minimap: {
    enabled: true // 우측 스크롤 미니맵
    },
    lineHeight: 20,

    }
    }

    /*<![CDATA[*/
    let monaco_test = new monaco.editor.create(document.getElementById('monaco'), setEditor(""));
    $('#monaco').height((monaco_test.getModel().getLineCount() * 19) + 10); // 19 = 줄 높이, 10 = 세로 스크롤 높이
    /*]]>*/


/////////////////////////////////////// 파일 트리 구조 설정  ///////////////////////////////////////////
    let savedState = [];

    function saveTreeState() {
    savedState = [];
    $('#tree').find('[data-role="node"]').each(function() {
    let expander = $(this).find('[data-role="expander"]');
    if (expander.attr('data-mode') === 'open') {
    savedState.push($(this).attr('data-id'));
    }
    });
    }
    function restoreTreeState() {
    $('#tree').find('[data-role="node"]').each(function() {
        let node = $(this);
        let id = node.attr('data-id');

        if (savedState.includes(id)) {
            node.find('[data-role="expander"]').attr('data-mode', 'open').find('i.gj-icon').removeClass('chevron-right').addClass('chevron-down');
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
    // 페이지 로드 시에 실행되도록
    document.addEventListener('DOMContentLoaded', function() {
    // 초기 파일 목록 불러오기
    loadFileList();
    });

//드래그 우클릭방지에 필요
var isLeftMouseDown = false;
    function loadFileList() {
    axios.get('/java/fileList').then(response => {
        const fileList = response.data;
        if ($('#left-pane').find('#tree').length === 0) {
            $('#left-pane').append('<div id="tree"></div>');
        }
        console.log(fileList);


        // 새로운 파일 목록으로 트리뷰 재구성
        var tree = $('#tree').tree({
            primaryKey: 'id',
            uiLibrary: 'materialdesign',
            dataSource: transformToTreeViewFormat(fileList),
            imageUrlField: 'flagUrl',
            dragAndDrop: true, // 드래그 앤 드롭 활성화
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
            var dragFolderHref = parent.text.match(/href='([^']*)'/)[1];

// 마지막 '\'의 인덱스를 찾음
            var lastBackslashIndex = dragFolderHref.lastIndexOf('/');

// '\' 다음의 문자열을 추출
            var lastPart = dragFolderHref.substring(lastBackslashIndex + 1);

// 추출한 문자열 중에 '.'이 포함되어 있다면 '.' 이전까지의 부분만 유지
            if (lastPart.includes('.')) {
                dragFolderHref  = dragFolderHref.substring(0, lastBackslashIndex)+'/';

                console.log("자름: "+dragFolderHref);
            } else {
                console.log("안자름: "+dragFolderHref);  // '.'이 없으면 원래 문자열 유지
            }

            console.log("폴더위치: "+dragFolderHref);
            // 추가: 파일인 경우에만 이동 요청을 서버로 보냄
            let DragFile = { 'filehref': dragFileHref, 'folderhref': dragFolderHref };
            axios.post("/java/drag", DragFile)
                .then((response) => {
                    saveTreeState();
                    $('#tree').remove();
                    loadFileList();
                })
                .catch((error) => {
                    // showFileList();
                    console.error("에러응답:", error.response);
                });
        });

        // 트리 재구성 후 상태 복원
        restoreTreeState();
        treeEvent();
        updateTreeView();
        }).catch(error => {
            console.error('Error fetching file list:', error);
        });
    }

function isDropAllowed(data, parent) {
    // 여기에 특정 조건을 추가하여 드롭을 허용할지 여부를 결정
    // 예: 폴더인 경우에만 드롭을 허용하도록 설정
    return parent.flagUrl.includes('folder.svg' || 'folder_open_FILL0_wght400_GRAD0_opsz24.svg');
}

    // FileNode 객체를 트리뷰 형식으로 변환
function transformToTreeViewFormat(fileList) {
    var treeData = [];

        // convertNode(fileNode, treeData, 1);// 재귀적으로 노드를 변환합니다. 재귀적- 함수내에서 같은 함수를 호출하는것
    fileList.forEach(function(fileNode) {
        convertNode(fileNode, treeData, 1);

    });
    return treeData;
}

function convertNode(fileNode, treeData, parentId) {


    var nodeId;

    // 이미지의 URL을 통해 파일과 폴더를 구분
    var isFolder = fileNode.flagUrl.includes('folder.svg' || 'folder_open_FILL0_wght400_GRAD0_opsz24.svg');

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
    // fileNode.children.forEach(function(child) {
    //     convertNode(child, node.children, nodeId + 1);
    //     nodeId++;
    // });

    treeData.push(node);
}

// document.addEventListener('DOMContentLoaded', function() {
    //     const treeArea = document.querySelector('#tree');
    //
    //     treeArea.addEventListener('click', function(event) {
    //         if (event.target.tagName === 'A') {
    //             event.preventDefault(); // 클릭에 의한 기본 동작(링크 이동)을 방지합니다.
    //         }
    //     });
    //
    //     treeArea.addEventListener('dblclick', function(event) {
    //         if (event.target.tagName === 'A') {
    //             console.log('더블클릭된 링크:', event.target.href); // 콘솔에 링크 출력
    //             // 더블클릭에 대한 추가적인 동작을 여기에 구현합니다.
    //         }
    //
    //     });
    // });
    // document.addEventListener('DOMContentLoaded', function() {폴

    var contextFilePath ="";
    function treeEvent() {
        const treeArea = document.querySelector('#tree');

        //////////////////////////// a 태그 이동 막기 /////////////////////////////
        treeArea.addEventListener('click', function(event) {
            if (event.target.closest('a')) {
                event.preventDefault();
            }
            updateTreeView();
        });

        //컨텍스트 메뉴를 여는 이벤트. 우클릭시 해당 파일의 href값을 rehandleFileSelection의 reselected에 저장.
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



/////////////////////////////////////// 더블클릭해서 파일 내용 불러오기 ////////////////////////////////////////////
        treeArea.addEventListener('dblclick', function(event) {

            // 에디터의 내용 변경이 있으면, 저장할건지 확인창 띄우기
            if(document.getElementById('selectedFileName').textContent!=='' && oriContent!==monaco_test.getValue()){
                var userResponse = confirm("에디터 내용이 수정되었습니다. \n저장하시겠습니까?");
                if (userResponse) {
                    // 사용자가 '예'를 클릭한 경우 / 저장 로직 필요
                    saveFile();
                    console.log("저장됨")
                } else {
                    // 사용자가 '아니오'를 클릭한 경우 / 아래 코드로 바로 진행
                }

            }

            const anchor = event.target.closest('a');
            if (anchor) {
                const filepath = anchor.getAttribute('href'); // 파일명 추출
                const filename = anchor.textContent;
                console.log("filename")
                console.log(filepath)
                console.log(filename)
                axios.post('/api/readFile', null, {
                    params: { filename2: filepath }
                })
                    .then(response => {
                        const anchorTags = treeArea.querySelectorAll('a');

                        anchorTags.forEach(anchor => {
                            anchor.style.fontWeight = 'normal';
                        });
                        const fileContent = response.data.fileContent;
                        monaco_test.setValue(fileContent); // 에디터에 내용 설정
                        oriContent = fileContent; // 에디터 초기 값 저장하기
                        document.getElementById('selectedFileName').textContent = filename;
                        document.getElementById('selectedFileName').title = filepath;

                        anchor.style.fontWeight = 'bold';
                        $('#lastSaveTime').text('저장 : '+response.data.lastModifiedTime); // 에디터 하단 저장 날짜 설정
                    })
                    .catch(error => console.error('Error fetching file content:', error));
            }
        });

//////////////////////////////////// 더블클릭해서 폴더 열고 닫기  /////////////////////////////////////////
        treeArea.addEventListener('dblclick', function(event) {
            const anchor = event.target.closest('a');
            if (anchor) {
                const wrapper = anchor.closest('[data-role="wrapper"]');
                const expander = wrapper.querySelector('[data-role="expander"]');
                const image = wrapper.querySelector('[data-role="image"] img');
                const icon = expander.querySelector('i');
                const childUl = wrapper.parentElement.querySelector('ul');

                if (expander && childUl) {
                    const currentMode = expander.getAttribute('data-mode');
                    if (currentMode === 'open') {
                        expander.setAttribute('data-mode', 'close');
                        childUl.style.display = 'none';
                        image.src = '/static/img/icon/folder.svg';
                        icon.classList.remove('chevron-down');
                        icon.classList.add('chevron-right');

                    } else {
                        expander.setAttribute('data-mode', 'open');
                        childUl.style.display = 'block';
                        image.src = '/static/img/icon/folder_open_FILL0_wght400_GRAD0_opsz24.svg';
                        icon.classList.remove('chevron-right');
                        icon.classList.add('chevron-down');

                    }
                }
            }
        });

    }

function removeFirstSegment(path) {
    const segments = path.split('/');
    segments.splice(1, 1);
    return  segments.join('/');
}


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
    if (selectedFile) {
        document.getElementById('selectedFile').value = selectedFile;
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


function openFileInfoModal() {

        // console.log("chk >>>>>> "+contextFilePath);

    if (selectedFile) {

        axios.post('/api/readFile', null, {
            params: { filename2: contextFilePath }
        })
            .then(response => {

                // console.log("response.data")
                // console.log(response.data)
                document.getElementById('fileInfo_size').textContent = response.data.fileSize;
                document.getElementById('fileInfo_creation').textContent = response.data.creationTime;
                document.getElementById('fileInfo_modify').textContent = response.data.lastModifiedTime;
            })
            .catch(error => console.error('Error fetching file content:', error));

        document.getElementById('fileInfo_name').textContent = selectedFile; // o
        document.getElementById('fileInfo_path').textContent = contextFilePath;  // o


        document.getElementById('fileInfoModal').style.display = 'block';
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
    document.getElementById('renameFileModal').style.display = 'none';
}
function closeFileInfoModal() {
    document.getElementById('fileInfoModal').style.display = 'none';
}
function closeRenameFolderModal() {
    document.getElementById('renameFolderModal').style.display = 'none';
}

// 파일 이름 변경 함수
function renameFile() {
    const reextensionSelect = document.getElementById("reextension"); // 파일확장자 값
    const currentFilename = document.getElementById("selectedFile").value; // 현재 파일이름명 값
    const newFileSet = document.getElementById("newFilename").value; //새로 입력한 파일이름 값 (파일이름명 규칙 때문에 따로 만듬)
    const currentFileAndFolder = reselectedFile; // 선택한 파일의 href값

    //파일이름명 규칙 실행
    if (!isValidFilename2(newFileSet.slice(0,-5))) {
        alert("파일명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
        return; // 추가 실행 중단
    }
    // 마지막 역슬래시 이후의 경로 부분 추출
    const lastBackslashIndex = currentFileAndFolder.lastIndexOf("\\");
    //href값에서 뒤의 파일명은 제거한 폴더 경로값만 추출
    const currentFolder = lastBackslashIndex !== -1 ? currentFileAndFolder.substring(0, lastBackslashIndex + 1) : "";
    console.log(lastBackslashIndex);
    console.log(currentFolder);

    // 파일을 백엔드에서 이름을 변경하도록 AJAX 요청을 보냅니다.
    axios.post("/api/rename", null, {
        params: {
            currentFilename: currentFilename,
            newFilename: newFileSet,
            currentFolder: currentFolder
        }
    })
        .then((response) => {
            $('#tree').remove();
            $("#newFilename").val("");
            loadFileList();
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

function renameFolder() {
    const currentFoldername = document.getElementById("selectedFolder").value; //현재폴더명
    const newFolderSet = document.getElementById("newFoldername").value; //새로 입력한 파일이름 값 (파일이름명 규칙 때문에 따로 만듬)
    const currentFolder = reselectedFolder; // 선택한 파일의 href값
    console.log("reselectedFile"+reselectedFolder)
    console.log("newFolderSet: "+newFolderSet);
    console.log("currentFolder: "+currentFolder);
    console.log("currentFoldername: "+currentFoldername);

    //파일이름명 규칙 실행
    if (!isValidFoldername(newFolderSet)) {
        alert("폴더명에는 특수 문자 및 일부 예약어를 사용할 수 없습니다.");
        return; // 추가 실행 중단
    }
    // 마지막 역슬래시 이후의 경로 부분 추출
    //href값에서 뒤의 파일명은 제거한 폴더 경로값만 추출
    console.log("currentFolder"+currentFolder);

    // 파일을 백엔드에서 이름을 변경하도록 AJAX 요청을 보냅니다.
    axios.post("/api/renamefolder", null, {
        params: {
            currentFoldername: currentFoldername,
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
function isValidFilename3(newfilename) {
    // 사용할 수 없는 문자와 규칙을 정의합니다.
    const invalidChars = /[\/:*?"<>|]/;
    const invalidNames = ['con', 'prn', 'aux', 'nul', 'com1', 'com2', 'com3', 'com4', 'com5', 'com6', 'com7', 'com8', 'com9', 'lpt1', 'lpt2', 'lpt3', 'lpt4', 'lpt5', 'lpt6', 'lpt7', 'lpt8', 'lpt9'];

    // 사용자가 입력한 파일 이름을 확인합니다.
    if (!newfilename || invalidChars.test(newfilename) || invalidNames.includes(newfilename.toLowerCase()) || newfilename.includes('..')) {
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


/////////////////////////////////////// 새파일 추가 ////////////////////////////////////////
document.getElementById("newFile2").addEventListener("click", function (){
    saveTreeState();
    let newFile = document.getElementById("newFile").value;
    let newMonaco =  "";
    let memo = { 'filename': newFile, 'monaco': newMonaco };
    axios.post("/api/newFile", memo).then((response) => {
        // 저장 후 파일 목록 다시 불러오기
        $('#tree').remove(); // 트리를 완전히 제거합니다.
        loadFileList();
        fileload(newFile);
        modal2.style.display = 'none';
    }).catch((error) => {
        console.log(error);
    });
});

function fileload(filename) {
    axios.post('/api/readFile', null, {
        params: { filename2: filename }
    }).then((response) => {
            const fileContent = response.data;
            monaco_test.setValue(fileContent); // 에디터에 내용 설정
            document.getElementById('selectedFileName').textContent = filename;
            document.getElementById('selectedFileName').title = removeFirstSegment(filename);
            console.log("여기까쥐!");
        })
        .catch((error) => {
            console.error('Error fetching file content:', error);
        });
}


/////////////////////////////////////// 모달창 및 사이드메뉴 ////////////////////////////////////////

const btn = document.getElementById('popupBtn');
const modal = document.getElementById('modalWrap');
const closeBtn = document.getElementById('closeBtn');

const btn2 = document.getElementById('popupBtn2');
const modal2 = document.getElementById('modalWrap2');
const closeBtn2 = document.getElementById('closeBtn2');

const btn3 = document.getElementById('popupBtn3');
const modal3 = document.getElementById('modalWrap3');
const closeBtn3 = document.getElementById('closeBtn3');

const modal4 = document.getElementById('renameFileModal');
const modal5 = document.getElementById('fileInfoModal');
const modal6 =document.getElementById('renameFolderModal');

const icon = document.getElementById('iconNav');
const balloon = document.getElementById('balloon');

const icon2 = document.getElementById('moreNav');
const balloon2 = document.getElementById('balloon2');

btn.onclick = function() {
    modal.style.display = 'block';
}
closeBtn.onclick = function() {
    modal.style.display = 'none';
}

window.onclick = function(event) {
    if (event.target == modal || event.target == modal2 || event.target == modal3 || event.target == modal4 || event.target == modal5 || event.target == modal6) {
        modal.style.display = "none";
        modal2.style.display = "none";
        modal3.style.display = "none";
        modal4.style.display = "none";
        modal5.style.display = "none";
        modal6.style.display = "none";
    }
}

btn2.onclick = function() {
    modal2.style.display = 'block';
    document.getElementById('filename').focus();
}
closeBtn2.onclick = function() {
    modal2.style.display = 'none';
}

btn3.onclick = function() {
    modal3.style.display = 'block';
}
closeBtn3.onclick = function() {
    modal3.style.display = 'none';
}


icon.addEventListener('mouseenter', function (){
    balloon.style.display = 'block';
})

icon.addEventListener('mouseover', function (){
    console.log("bye");
    balloon.style.display = 'block';
})

icon.addEventListener('mouseout', function (){
    balloon.style.display = 'none';
})


icon2.addEventListener('mouseenter', function (){
    balloon2.style.display = 'block';
})

icon2.addEventListener('mouseover', function (){
    balloon2.style.display = 'block';
})

icon2.addEventListener('mouseout', function (){
    balloon2.style.display = 'none';
})

/////////////////////////////////////// ZIP 파일로 다운로드 ////////////////////////////////////////
// function zipDownload() {
//     document.getElementById('saveZip').addEventListener('click', function () {
//         window.location.href = '/java/download-zip';
//     });
// }


///////////////////////////////////////마우스 우클릭 메뉴(contextMenu) ////////////////////////////////////////

$.contextMenu({
    selector: '[data-role="node"]',

    items: {
        item1: {
            name: '파일 생성',
            icon: 'fa-solid fa-file',
            callback: function (key, options) {
                console.log("key", key);
                console.log("options", options);

                modal2.style.display = 'block';
                document.getElementById('filename').focus();

            }
        },
        item2: {
            name: '폴더 생성',
            icon: 'fa-solid fa-folder',
            callback: function (key, options) {
                console.log("key", key);
                console.log("options", options);

                modal.style.display = 'block';

            }
        },
        item3: {
            name: '이름 변경',
            icon: 'fa-solid fa-pen-to-square',
            callback: function (key, options) {
                var $trigger = $(options.$trigger);
                console.log("트리거값: "+$trigger)
                var filename =  $trigger.attr('data-id')
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
                var filename = $trigger.find('a').attr('href')
                return filename.includes('.java');
            },
            callback : function (key, options) {
                console.log("key", key);
                console.log("options", options);
                var $trigger = options.$trigger;
                var filename = $trigger.find('a').attr('href')
                // span 안의 a 태그의 텍스트를 가져옴
                console.log("Clicked on " + key + " for element with filename: " + filename);
                var filename2 = filename.split(/[\\/]/).pop().replace(/\.[^.]+$/, '');
                console.log(filename2);
                axios.post("/api/fileDownload", {filename: filename}, {responseType: 'blob'})
                    .then(response => {
                        const url = window.URL.createObjectURL(new Blob([response.data]));
                        const link = document.createElement('a');
                        link.href = url;
                        // link.setAttribute('download', filename);
                        link.setAttribute('download', filename2+".java");
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
                var filename = $trigger.find('a').attr('href')
                return !filename.includes('.java');
            },
            callback: function (key, options) {
                console.log("key", key);
                console.log("options", options);
                var $trigger = options.$trigger;
                var filename = $trigger.find('a').attr('href')
                // span 안의 a 태그의 텍스트를 가져옴
                console.log("Clicked on " + key + " for element with filename: " + filename);
                var filename2 = filename.split(/[\\/]/).pop().replace(/\.[^.]+$/, '');
                console.log(filename2);
                axios.post("/api/zipDownload", {filename: filename}, {responseType: 'blob'})
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
                // 메뉴 아이템을 클릭한 경우의 동작
                console.log("key", key);
                console.log("options", options);

                var $trigger = options.$trigger;
                var filename = $trigger.find('a').attr('href')
                // span 안의 a 태그의 텍스트를 가져옴
                console.log("Clicked on " + key + " for element with filename: " + filename);

                axios.post("/api/deleteFile", { filename: filename }).then((response) => {
                    $('#tree').remove();
                    loadFileList();
                    console.log("삭제됨");
                }).catch((error) => {
                    console.log(error);
                });
            }
        },
        item7: {
            name: '정보',
            icon:'fa-solid fa-circle-info',
            callback: function (key, options) {
                console.log(key);
                console.log(options);
                console.log("정보");
                openFileInfoModal(); // 모달 열기
            }
        }
    }
});



/////////////////////////////////////////////////// 자동 저장 //////////////////////////////////////////////////////////
const checkbox = document.getElementById("autoSaveCheck");
let auto; // 변수 선언

checkbox.addEventListener("change", function() {

    // 파일 미선택시 처리
    if (document.getElementById('selectedFileName').textContent === '') {
        alert("선택된 파일이 없습니다.");
        checkbox.checked = false;
        return false;
    }

    if (checkbox.checked) {
        clearInterval(auto);
        console.log("자동저장중");

        auto = setInterval(autoSave,10000);

        chkModified();
    } else {
        clearInterval(auto);
        console.log("자동저장해제");
        // setInterval(saveToLocalStorage, 4000);
    }
});
function autoSave() {

    var fileName = document.getElementById('selectedFileName').title // 파일 경로
    var mid = document.getElementById("user_mid").value;

    var filePath = "/"+ fileName;
    var fileContent = monaco_test.getValue();

    console.log("파일 경로 : "+fileName)
    console.log("에디터 내용 : "+monaco_test.getValue())

    $.ajax({
        type: "POST",
        url: "/api/saveFile",
        data: JSON.stringify({ "content": fileContent ,"filename":fileName}),
        contentType: "application/json",
        success: function(response) {
            // alert(response.message + '\n생성일: ' + response.creationTime + '\n수정일: ' + response.lastModifiedTime);
            console.log(response.message);
            console.log(response.creationTime);
            console.log(response.lastModifiedTime);
            $('#lastSaveTime').text('저장 : '+response.lastModifiedTime);
            oriContent = fileContent;
        },
        error: function(error) {
            // alert('파일 저장 실패: ' + error.responseText);
        }
    });
}

///////////////////////////////////// 파일 전환시 저장 여부 확인 /////////////////////////////////////////////
var oriContent = ""; // 처음 코드 내용 저장할 변수
// 파일 불러올때, 저장할 때마다 초기화 필요



///////////////////선택한 파일 이름 받기/////////
<!--선택한 파일 이름을 받음-->
let selectedFolder = null;

function handleFolderSelection(filename) {
    selectedFolder = filename;
    // 필요에 따라 추가적인 로직을 수행할 수 있습니다.
}

//////////////////////우클릭으로 이름변경시 값을 추출/////////
// let reselectedFolder = null;

// function rehandleFolderSelection(anchor2) {
//     if (selectedFolder) {
//         reselectedFolder = anchor2;
//
//         return reselectedFolder;
//         // 선택한 파일에 대한 추가적인 로직을 수행할 수 있습니다.
//     }
// }

//////////////폴더 이름 변경시 사용할수 없는 문자////////////
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
/////모달닫기//////
function closeRenameFolderModal() {
    document.getElementById('newFoldername').value = '';
    document.getElementById('renameFolderModal').style.display = 'none';
}
////////////////////////////폴더이름변경///////////////////
function renameFolder() {
    const currentFoldername = document.getElementById("selectedFolder").value; //현재폴더명
    const newFolderSet = document.getElementById("newFoldername").value; //새로 입력한 파일이름 값 (파일이름명 규칙 때문에 따로 만듬)
    const currentFolder = reselectedFolder; // 선택한 파일의 href값
    console.log("reselected: "+ reselectedFolder)
    const lastBackslashIndex3 = reselectedFolder.lastIndexOf('\\');
    console.log("last: " + lastBackslashIndex3)
    const folder = reselectedFolder.substring(0, lastBackslashIndex3);
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
    axios.post("/java/renamefolder", null, {
        params: {
            currentFoldername: folder,
            newFoldername: newFolderSet, //새로지은폴더명
            currentFolder: currentFolder //현재경로+이전이름
        }
    })
        .then((response) => {
            document.getElementById('newFoldername').value = ''
            showFileList();
            alert("폴더명 변경 완료");
        })
        .catch((error) => {
            console.error("에러 응답:", error.response); // 에러 응답 출력
            if (error.response && error.response.data) {
                alert(error.response.data);
            } else {
                console.error("에러:", error.message);
            }
        })
        .finally(() => {
            closeRenameFolderModal(); // AJAX 요청 완료 후 모달 닫기
        });
}
//////////////////폴더이름 변경 모달열기///////////
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

///////////////저장소 리로딩/////////
function showFileList() {
    saveTreeState();
    $('#tree').remove();
    loadFileList();
    // 파일 목록을 불러오는 함수 호출
    // 모달 열기
    openModal();
}

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
//
// <ul className="gj-list gj-list-md gj-unselectable gj-tree-drag-el gj-tree-md-drag-el"
// data-role="draggable-clone" data-type="draggable" data-guid="d74bda09-86f8-14a7-97d1-8962a967d88b"
// data-draggable="false" draggable-dragging="false"
// style="position: fixed; top: 227px; left: 989px; width: 245.009px;"
// draggable-x="1077" draggable-y="258">
// <li className="undefined">
// <div data-role="wrapper">
// <span data-role="indicator" className=""></span>
// <span data-role="display" className=""><a href="/._dir1">._dir1
// </a></span></div></li></ul>

// data-draggable="true"

