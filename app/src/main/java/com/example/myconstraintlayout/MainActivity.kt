package com.example.myconstraintlayout

//lay anh tu internet
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.myconstraintlayout.ui.theme.MyConstraintLayoutTheme
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL
import androidx.work.Worker
import androidx.work.WorkerParameters
//
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val uploadWorkRequest: WorkRequest =
//            OneTimeWorkRequestBuilder<ParseXmlWorker>().build() //Create a WorkRequest
//        WorkManager.getInstance(this)
//            .enqueue(uploadWorkRequest) //Submit the WorkRequest to the system
        RequestXML(this)
        setContent {
            MyConstraintLayoutTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFf0f2f5)
                ) {
                    MyScreen(this)
                }
            }
        }
    }
}

fun RequestXML(context: Context) {
//    val context=LocalContext.current
    val uploadWorkRequest: WorkRequest =
        OneTimeWorkRequestBuilder<ParseXmlWorker>().build() //Create a WorkRequest
    WorkManager.getInstance(context)
        .enqueue(uploadWorkRequest) //Submit the WorkRequest to the system
}

/////////////////////////////////////////////////
class ParseXmlWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        getXmlData()
        return Result.success()
    }
}

//lấy dữ liệu rss từ internet
fun getXmlData() {
    // Parse the XML data
    var crushemt = mutableListOf<MutableMap<String, Any>>()
    val parserFactory = XmlPullParserFactory.newInstance()
    val parser = parserFactory.newPullParser()
    parser.setInput(URL(url.value).openStream(), "UTF-8")
    var eventType = parser.eventType
    var isParsingItem = false
    var title: Any = ""
    var description: Any = ""
    var link: Any = ""
    var pubDate: Any = ""
    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                val tagName = parser.name
                if (tagName == "item") {
                    isParsingItem = true
                } else if (isParsingItem) {
                    when (tagName) {
                        "title" -> title = parser.nextText()
                        "description" -> description = parser.nextText()
                        "link" -> link = parser.nextText()
                        "pubDate" -> pubDate = parser.nextText()
                    }
                }
            }

            XmlPullParser.END_TAG -> {
                val tagName = parser.name
                if (tagName == "item") {
                    // Do something with the values, such as creating a new object or map
                    val newsMap = mutableMapOf(
                        "title" to title,
                        "description" to description,
                        "pubDate" to pubDate,
                        "link" to link,
                        "imageNews" to getSrc(description.toString()),
                    )
                    crushemt.add(newsMap)
                    // Do something with the newsMap, such as adding it to a list
                    isParsingItem = false
                    title = ""
                    description = ""
                    link = ""
                    pubDate = ""
                }
            }
        }
        eventType = parser.next()
    }
    newsLists.value = crushemt
    Log.d("ok", newsLists.value.toString())
}

// lấy string ảnh
fun getSrc(html: String): String {
    val regex = Regex("src=\"(.*?)\"")
    val match = regex.find(html)
    return match?.groupValues?.get(1) ?: ""
}

// đổi dạng ngày tháng năm
fun changeDateFormat(pubDate: Any?): String {
    val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)
    val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)

    val inputDate = "$pubDate"
    val parsedDate = inputFormat.parse(inputDate)

    return outputFormat.format(parsedDate)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlideItem(listImgd: MutableState<MutableList<MutableMap<String, Any>>>) {
    val listImg: MutableList<MutableMap<String, Any>> = listImgd.value.take(4).toMutableList()
//        listOf(
//        "https://cdn-i.vtcnews.vn/resize-v1/HqZLQNeNaVu3XqeVHms9Vi-KMugXObbWkWjIW669eAU/upload/2023/06/19/dong-thanh-huyen-07240487.jpg",
//        "https://cdn-i.vtcnews.vn/resize-v1/4sTZ8qldeIUtJ67-2gi5fTbZzgPxX3oBp8fZ3P4h9kc/upload/2023/06/18/chay-xe-binh-duong-1-20050057.jpg",
//        "https://cdn-i.vtcnews.vn/resize-v1/rNNq4G6MIePyY2njJEWUQqmU-Hkm6Wh9QqK4gxWrtPw/upload/2023/06/19/thu-tuong-trung-quoc-tham-duc-11054564.jpg",
//        "https://cdn-i.vtcnews.vn/resize-v1/D4KhES4ksr9oL9kKUwDDabSNilmVde5XQmWUzFp1kCk/upload/2023/06/19/bou-samnang-09553845.jpeg"
//    )
    Log.d("listimg", listImgd.value.toString())
    var pagerState =
        rememberPagerState(initialPage = 4)//initialPage - The pager that should be shown first.
    var scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.initialPage
            scope.launch {
                if (nextPage == 0) {
                    pagerState.animateScrollToPage(0)
                } else {
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.0.dp)
            .height(200.0.dp)
    ) {
        LazyRow(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(2f)
        ) {
            items(listImg.size) { item ->
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .padding(3.0.dp)
                        .clip(CircleShape)
                        .background(if (item == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary)
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            pageCount = listImg.size,
        ) { page ->
            ElevatedCard(
                modifier = Modifier.size(width = Dp.Infinity, height = 200.dp)
            ) {
                AsyncImage(
                    model = listImg[page]["imageNews"],
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

@Composable
fun LoadWebview(url: String) {
    AndroidView(factory = {
        WebView(it).apply {
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoadWebview("https://vtc.vn/rss/thoi-su.rss")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardNews(title: Any?, desc: Any?, pubDate: Any?, link: Any?, img: Any?) {
    val outputDate: String = changeDateFormat(pubDate)
    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(link.toString())) }
    val uriHandler = LocalUriHandler.current
    val loadWebUrl = @Composable { url: String ->
        AndroidView(factory = {
            WebView(it).apply {
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        })
    }
    Card(
        onClick = {
//            context.startActivity(intent)
            uriHandler.openUri(link.toString())
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = RectangleShape,
        modifier = Modifier
            .width(width = Dp.Infinity)
            .height(IntrinsicSize.Max),
        content = {
            Column {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.0.dp, vertical = 8.0.dp)
                        .weight(4f)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            modifier = Modifier.padding(bottom = 8.0.dp),
                            text = outputDate,
                            fontFamily = FontFamily.Serif,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Thin,
                        )
                        Text(
                            text = "$title",
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    AsyncImage(
                        modifier = Modifier
                            .clip(RectangleShape)
                            .weight(1f),
                        contentScale = ContentScale.Fit,
                        model = img,
                        contentDescription = null,
                    )
                }
            }
        }
    )
}

@Composable
fun openWebPage(link: String) {
    loadWebUrl(LocalContext.current, link)
}

@Composable
fun loadWebUrl(context: Context, url: String) {
    AndroidView(factory = {
        WebView(context).apply {
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    })
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MyScreen(context: Context) {
    //Create a WorkRequest to parse the XML data
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val newsList: MutableState<MutableList<MutableMap<String, Any>>> = newsLists
    Log.d("ok2", newsList.value.toString())
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "NPaperCrush")
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Home, contentDescription = null,
                            modifier = Modifier.size(55.dp)
                        )
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                ScrollableTabRow(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 8.0.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            color = Color.Transparent,
                            height = 4.dp,
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                .padding(bottom = 8.0.dp)
                        )
                    },
                    divider = {
                        Divider(
                            thickness = 1.5.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                ) {
                    listMenu.forEachIndexed { index, title ->
                        Box() {
                            Tab(
                                selectedContentColor = Color.White,
                                unselectedContentColor = Color.Black,
                                text = { Text(title) },
                                selected = selectedTabIndex == index,
                                onClick = {
                                    url.value = listLinkMenu.value[index]
                                    RequestXML(context)
                                    selectedTabIndex = index
                                },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 8.0.dp)
                                    .background(
                                        if (selectedTabIndex == index) Color.Black else Color.White,
                                        shape = RoundedCornerShape(20.0.dp)
                                    )
                                    .border(
                                        color = if (selectedTabIndex == index) Color.Black else Color.White,
                                        width = 1.0.dp,
                                        shape = RoundedCornerShape(20.0.dp)
                                    ),
                            )
                        }
                    }
                }
                SlideItem(newsLists)
                LazyColumn(
                    modifier = Modifier.padding(8.0.dp),
                    state = LazyListState()
                ) {
                    items(newsList.value) { item ->
                        if (item != null)
                            CardNews(
                                item["title"],
                                item["description"],
                                item["pubDate"],
                                item["link"],
                                item["imageNews"]
                            )
                        else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    )
}


