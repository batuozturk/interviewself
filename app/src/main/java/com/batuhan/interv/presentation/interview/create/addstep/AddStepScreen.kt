package com.batuhan.interv.presentation.interview.create.addstep

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.batuhan.interv.R
import com.batuhan.interv.data.model.InterviewStep
import com.batuhan.interv.data.model.Question
import com.batuhan.interv.ui.theme.fontFamily
import com.batuhan.interv.util.BaseView
import com.batuhan.interv.util.DialogData
import com.batuhan.interv.util.isTablet
import com.batuhan.interv.util.keyboardAsState
import kotlinx.coroutines.launch

@Composable
fun AddStepScreen(
    interviewId: Long,
    language: String,
    onBackPressed: () -> Unit,
    showDialog: (DialogData) -> Unit = {},
    clearDialog: () -> Unit = {},
) {
    val context = LocalContext.current

    val viewModel = hiltViewModel<AddStepViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val questions = viewModel.questions.collectAsLazyPagingItems()
    val steps = viewModel.steps.collectAsLazyPagingItems()

    val dialogData by remember(uiState.dialogData) {
        derivedStateOf { uiState.dialogData }
    }

    val searchText by remember(uiState.searchText) {
        derivedStateOf { uiState.searchText }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                is AddStepEvent.Back -> onBackPressed.invoke()
                is AddStepEvent.AddStep -> viewModel.addStep(it.question)
                is AddStepEvent.DeleteStep -> viewModel.deleteStep(it.interviewStep)
                is AddStepEvent.ClearDialog -> clearDialog.invoke()
                is AddStepEvent.Search -> viewModel.search(it.searchText)
            }
        }
    }

    val isTablet by remember(context.isTablet()) {
        derivedStateOf { context.isTablet() }
    }
    if (isTablet) {
        BackHandler {
            viewModel.sendEvent(AddStepEvent.Back)
        }
        LaunchedEffect(key1 = Unit) {
            viewModel.initInterviewId(interviewId)
            viewModel.initLanguage(language)
        }
        LaunchedEffect(dialogData) {
            dialogData?.let(showDialog)
        }
    }
    if (isTablet) {
        AddStepScreenContent(questions, steps, searchText, viewModel::sendEvent)
    } else {
        BaseView(dialogData = dialogData) {
            AddStepScreenContent(questions, steps, searchText, viewModel::sendEvent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddStepScreenContent(
    questions: LazyPagingItems<Question>,
    steps: LazyPagingItems<InterviewStep>,
    searchText: String,
    sendEvent: (AddStepEvent) -> Unit,
) {
    val pagerState =
        rememberPagerState {
            2
        }

    val currentPage by
        remember(pagerState.currentPage) {
            derivedStateOf { pagerState.currentPage }
        }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.create_interview_add_step_title),
                        fontFamily = fontFamily,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { sendEvent.invoke(AddStepEvent.Back) }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(it),
        ) {
            TabRow(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 16.dp),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                selectedTabIndex = currentPage,
                divider = {},
                indicator = {
                    if (currentPage < it.size) {
                        Column(
                            modifier =
                                Modifier
                                    .tabIndicatorOffset(it[currentPage])
                                    .fillMaxSize()
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.onSurface,
                                        RoundedCornerShape(10.dp),
                                    ),
                        ) {
                        }
                    }
                },
            ) {
                Tab(
                    modifier = Modifier.height(48.dp).padding(12.dp),
                    selected = currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(0)
                        }
                    },
                ) {
                    Text(stringResource(R.string.questions), textAlign = TextAlign.Center)
                }
                Tab(
                    modifier = Modifier.height(48.dp).padding(12.dp),
                    selected = currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(1)
                        }
                    },
                ) {
                    Text(stringResource(R.string.added_steps), textAlign = TextAlign.Center)
                }
            }
            HorizontalPager(state = pagerState, userScrollEnabled = false) {
                when (it) {
                    0 -> SelectQuestion(questions = questions, searchText = searchText, sendEvent)
                    1 -> AddedStepList(steps = steps, sendEvent)
                }
            }
        }
    }
}

@Composable
fun SelectQuestion(
    questions: LazyPagingItems<Question>,
    searchText: String,
    sendEvent: (AddStepEvent) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        QuestionSearchView(searchText) {
            sendEvent(AddStepEvent.Search(it))
        }
        LazyColumn {
            items(questions.itemCount) {
                questions[it]?.let { question ->
                    QuestionSelectItem(question = question, sendEvent)
                }
            }
        }
    }
}

@Composable
fun AddedStepList(
    steps: LazyPagingItems<InterviewStep>,
    sendEvent: (AddStepEvent) -> Unit,
) {
    LazyColumn(
        Modifier
            .fillMaxSize(),
    ) {
        items(steps.itemCount) {
            steps[it]?.let { step ->
                InterviewStepItem(step = step, sendEvent)
            }
        }
    }
}

@Composable
fun InterviewStepItem(
    step: InterviewStep,
    sendEvent: (AddStepEvent) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(step.question?.question?.lowercase() ?: "undefined", modifier = Modifier.weight(7f))
        IconButton(onClick = { sendEvent(AddStepEvent.DeleteStep(step)) },modifier = Modifier.weight(1f)) {
            Icon(Icons.Outlined.Delete, contentDescription = null)
        }
    }
}

@Composable
fun QuestionSelectItem(
    question: Question,
    sendEvent: (AddStepEvent) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp)
            .clickable {
                sendEvent(AddStepEvent.AddStep(question))
            }
            .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(question.question?.lowercase() ?: "undefined")
    }
}

@Composable
fun QuestionSearchView(
    searchText: String?,
    onSearch: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isKeyboardOpen by keyboardAsState()
    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
        }
    }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        placeholder = {
            Text(stringResource(id = R.string.search_questions))
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(),
        value = searchText ?: "",
        onValueChange = onSearch,
        singleLine = true,
    )
}
