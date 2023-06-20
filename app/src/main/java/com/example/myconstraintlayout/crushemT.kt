package com.example.myconstraintlayout

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData

var listMenu: List<String> = listOf(
    "Trang chủ",
    "Thời sự",
    "Thế giới",
    "Giáo dục",
    "Phóng sự - khám phá",
    "Địa ốc -Bất động sản",
    "Văn hóa - Giải trí",
    "Thể thao",
    "Doanh nhân- Doanh nghiệp",
    "Kinh tế",
    "Truyền hình",
    "Pháp luật",
    "Khoa học - Công nghệ",
    "Ô tô xe máy",
    "Sức khỏe đời sống",
    "Giới trẻ"
)
var listLinkMenu: MutableState<List<String>> = mutableStateOf(
    listOf(
        "https://vtc.vn/rss/feed.rss",
        "https://vtc.vn/rss/thoi-su.rss",
        "https://vtc.vn/rss/the-gioi.rss",
        "https://vtc.vn/rss/giao-duc.rss",
        "https://vtc.vn/rss/phong-su-kham-pha.rss",
        "https://vtc.vn/rss/bat-dong-san.rss",
        "https://vtc.vn/rss/van-hoa-giai-tri.rss",
        "https://vtc.vn/rss/the-thao.rss",
        "https://vtc.vn/rss/doanh-nghiep-doanh-nhan.rss",
        "https://vtc.vn/rss/kinh-te.rss",
        "https://vtc.vn/rss/truyen-hinh.rss",
        "https://vtc.vn/rss/phap-luat.rss",
        "https://vtc.vn/rss/khoa-hoc-cong-nghe.rss",
        "https://vtc.vn/rss/oto-xe-may.rss",
        "https://vtc.vn/rss/suc-khoe.rss",
        "https://vtc.vn/rss/gioi-tre.rss"
    )
)
var url: MutableState<String> = mutableStateOf(value = listLinkMenu.value[0])

// Khởi tạo một danh sách rỗng
var newsLists: MutableState<MutableList<MutableMap<String, Any>>> = mutableStateOf(mutableListOf())

//fun getSrc(html: String): String {
//    val regex = Regex("src=\"(.*?)\"")
//    val match = regex.find(html)
//    return match?.groupValues?.get(1) ?: ""
//}

//fun main(args: Array<String>) {
//    val html = """<![CDATA[ <a title="Bộ Quốc phòng Nga đặt mua thêm hệ thống vũ khí nhiệt áp TOS" href="https://vtc.vn/bo-quoc-phong-nga-dat-mua-them-he-thong-vu-khi-nhiet-ap-tos-ar800375.html">
//                                                                                                    			<img alt="Bộ Quốc phòng Nga đặt mua thêm hệ thống vũ khí nhiệt áp TOS" src="https://cdn-i.vtcnews.vn/resize-v1/d_nhvHgMpfaAshRMN3T0Al37JeadfdaK1nldK2Cl7lQ/upload/2023/06/18/he-thong-vu-khi-nhiet-ap-tos-09455688.jpg" ></a></br>Theo Bộ trưởng quốc phòng Nga Sergei Shoigu, dây chuyền sản xuất các hệ thống vũ khí nhiệt áp TOS cần được mở rộng để đáp ứng nhu cầu từ chiến dịch quân sự đặc biệt. ]]>"""
//    val src = getSrc(html)
//    println(src)
//}
