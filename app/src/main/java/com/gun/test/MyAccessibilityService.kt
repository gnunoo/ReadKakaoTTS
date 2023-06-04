package com.gun.test

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class MyAccessibilityService : AccessibilityService() {
    // 이벤트가 발생할때마다 실행되는 부분
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 패키지 체크
        val packageName = event.packageName.toString()
        if (packageName != TARGET_APP_PACKAGE) return

        // 로그
//        Log.v(TAG, String.format(
//                "EVENT :: type=%s, class=%s, package=%s, time=%s, text=%s, source=%s",
//                event.getEventType(), event.getClassName(), event.getPackageName(),
//                event.getEventTime(), event.getText(), event.getSource()));

        // 정보 가져오기
        if (event.className == null || event.source == null) return
        var type = event.eventType
        var className = event.className.toString()
        var rootNode = event.source

        // 카카오톡 현재 보고있는 대화창 텍스트 읽기
        val message = StringBuilder()

        // CASE 1. 새로운 메시지가 온 경우
        if (type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && className == "android.widget.FrameLayout") {
            if (rootNode!!.childCount >= 1) {
                rootNode = rootNode.getChild(0)
                className = rootNode.className.toString()
                type = AccessibilityEvent.TYPE_VIEW_SCROLLED
            } else {
                return
            }
        }
        // CASE 2. 사용자가 스크롤한 경우
        if (type == AccessibilityEvent.TYPE_VIEW_SCROLLED && className == "androidx.recyclerview.widget.RecyclerView") {
            var node: AccessibilityNodeInfo
            var name: CharSequence? = null
            var text: CharSequence? = null
            for (i in 0 until rootNode!!.childCount) {
                node = rootNode.getChild(i)
                if ("android.widget.FrameLayout" != node.className.toString()) return
                val childCount = node.childCount

                // 날짜 노드
                if (childCount == 1 &&
                    isChildTextView(node, 0)
                ) {
                    continue  // 넘어감
                } else if (childCount == 1 &&
                    isChildLinearLayout(node, 0) &&
                    isChildTextView(node.getChild(0), 0) && node.getChild(0)
                        .getChild(0).text.toString() == "공지가 등록되었습니다."
                ) {
                    name = null
                    continue  // 넘어감
                } else if (childCount >= 3 &&  // 일반=3, 장문(전체보기 버튼)=4, URL=5
                    isChildButton(node, 0) &&
                    isChildTextView(node, 1) &&
                    (isChildRelativeLayout(node, 2) || isChildLinearLayout(node, 2)) &&
                    isChildTextView(node.getChild(2), 0)
                ) {
                    name = node.getChild(1).text
                    text = node.getChild(2).getChild(0).text
                } else if (childCount >= 4 &&  // 일반=4, 반응=5
                    isChildButton(node, 0) &&
                    isChildTextView(node, 1) &&
                    isChildFrameLayout(node, 2) &&
                    (isChildImageView(node.getChild(2), 0) || isChildRecyclerView(
                        node.getChild(2),
                        0
                    )) &&  // 1장 vs 여러장
                    isChildImageView(node, 3)
                ) {
                    name = node.getChild(1).text
                    text = "(사진)"
                } else if ((childCount == 3 || childCount == 4) &&  // 일반=3, 반응=4
                    isChildButton(node, 0) &&
                    isChildTextView(node, 1) &&
                    (isChildRelativeLayout(node, 2) && isChildImageView(
                        node.getChild(2),
                        0
                    ) ||  // 움직이는 이모티콘
                            isChildImageView(node, 2))
                ) { // 일반 이모티콘
                    name = node.getChild(1).text
                    text = "(이모티콘)"
                } else if (childCount >= 1 &&  // 일반=1, 반응=2, URL=3
                    isChildRelativeLayout(node, 0) &&
                    isChildTextView(node.getChild(0), 0)
                ) {
                    name = if (isSelfMessage(node.getChild(0))) "나" else name
                    text = node.getChild(0).getChild(0).text
                } else if (childCount == 2 &&
                    isChildImageView(node, 0) &&
                    isChildFrameLayout(node, 1)
                ) {
                    name = if (isSelfMessage(node.getChild(0))) "나" else name
                    text = "(사진)"
                } else if (childCount == 2 &&
                    isChildFrameLayout(node, 0) &&
                    isChildImageView(node.getChild(0), 0) &&
                    isChildImageView(node, 1)
                ) {
                    name = if (isSelfMessage(node.getChild(0))) "나" else name
                    text = "(사진)"
                    //                  final Rect rect = new Rect();
//                  node.getChild(0).getBoundsInScreen(rect);
//                  Log.i(TAG, "POS: " + rect.toString());
                } else if ((childCount == 1 || childCount == 2) &&  // 일반=1, 반응=2
                    (isChildRelativeLayout(node, 0) && isChildImageView(
                        node.getChild(0),
                        0
                    ) ||  // 움직이는 이모티콘
                            isChildImageView(node, 0))
                ) { // 일반 이모티콘
                    name = if (isSelfMessage(node.getChild(0))) "나" else name
                    text = "(이모티콘)"
                } else {
                    name = null
                    text = getAllText(node, 0)
                    // Log.e(TAG,"CHILD " + i + " // " + "unknown // childCnt="+ node.getChildCount() + ", text=" + text);
                    // printAllViews(node, 0);
                }

//                Log.v(TAG,"CHILD " + i + " // " + name + ": " + text);

//                message.append(text).append(" "); // 대화 내용만
                message.append(name).append(": ").append(text).append("\n") // 이름 + 대화 내용
            }
        }
        val m = message.toString()
        if (m.length > 0) {
            Log.e(TAG, m)
            val intent = Intent(ACTION_NOTIFICATION_BROADCAST)
            intent.putExtra(EXTRA_TEXT, m) // 메인 엑티비티로 전달
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    private fun checkChildClass(
        node: AccessibilityNodeInfo,
        index: Int,
        className: String
    ): Boolean {
        val child = node.getChild(index) ?: return false
        val name = child.className ?: return false
        return name.toString() == className
    }

    private fun isChildButton(node: AccessibilityNodeInfo, index: Int): Boolean {
        return checkChildClass(node, index, "android.widget.Button")
    }

    private fun isChildTextView(node: AccessibilityNodeInfo, index: Int): Boolean {
        return checkChildClass(node, index, "android.widget.TextView")
    }

    private fun isChildImageView(node: AccessibilityNodeInfo, index: Int): Boolean {
        return checkChildClass(node, index, "android.widget.ImageView")
    }

    private fun isChildRecyclerView(node: AccessibilityNodeInfo, index: Int): Boolean {
        return checkChildClass(node, index, "androidx.recyclerview.widget.RecyclerView")
    }

    private fun isChildFrameLayout(node: AccessibilityNodeInfo, index: Int): Boolean {
        return checkChildClass(node, index, "android.widget.FrameLayout")
    }

    private fun isChildLinearLayout(node: AccessibilityNodeInfo, index: Int): Boolean {
        return checkChildClass(node, index, "android.widget.LinearLayout")
    }

    private fun isChildRelativeLayout(node: AccessibilityNodeInfo, index: Int): Boolean {
        return checkChildClass(node, index, "android.widget.RelativeLayout")
    }

    private fun isSelfMessage(node: AccessibilityNodeInfo): Boolean { // 내가 보낸 메시지인지 체크
        val rect = Rect()
        node.getBoundsInScreen(rect) // 노드의 화면 위치를 기준으로 내가 보냈는지 상대가 보냈는지 알아냄
        return rect.left >= 200
    }

    public override fun onServiceConnected() {
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        info.feedbackType = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FEEDBACK_VISUAL;
//        info.notificationTimeout = 500;
//        setServiceInfo(info);
    }

    override fun onInterrupt() {
        Log.e(TAG, "onInterrupt()")
    }

    private fun printAllViews(nodeInfo: AccessibilityNodeInfo?, depth: Int) {
        if (nodeInfo == null) return
        if (depth > 10) return  // Max-Depth
        var t = ""
        for (i in 0 until depth) t += "."
        Log.d(
            TAG,
            t + "(" + nodeInfo.text + " <-- " + nodeInfo.viewIdResourceName + " / " + nodeInfo.className.toString() + ")"
        )
        for (i in 0 until nodeInfo.childCount) {
            printAllViews(nodeInfo.getChild(i), depth + 1)
        }
    }

    private fun getAllText(node: AccessibilityNodeInfo?, depth: Int): String {
        if (node == null) return ""
        if (depth > 5) return "" // Max-Depth
        val className = node.className
        val text = StringBuilder()
        if (className != null && "android.widget.TextView" == className.toString()) {
            if (node.text != null) {
                text.append(node.text.toString()).append(" ")
            }
        }
        for (i in 0 until node.childCount) {
            text.append(getAllText(node.getChild(i), depth + 1))
        }
        return text.toString()
    }

    companion object {
        private const val TAG = "AccessibilityService"
        const val ACTION_NOTIFICATION_BROADCAST = "MyAccessibilityService_LocalBroadcast"
        const val EXTRA_TEXT = "extra_text"
        const val TARGET_APP_PACKAGE = "com.kakao.talk" // 화면을 읽어올 앱
    }
}