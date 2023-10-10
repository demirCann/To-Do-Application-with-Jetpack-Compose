package com.demircandemir.todoapp.ui.screens.list

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.demircandemir.todoapp.R
import com.demircandemir.todoapp.data.models.Priority
import com.demircandemir.todoapp.data.models.Task
import com.demircandemir.todoapp.ui.theme.HighPriorityColor
import com.demircandemir.todoapp.ui.theme.LARGE_PADDING
import com.demircandemir.todoapp.ui.theme.PRIORITY_INDICATOR_SIZE
import com.demircandemir.todoapp.ui.theme.TASK_ITEM_ELEVATİON
import com.demircandemir.todoapp.ui.theme.taskItemBackgroundColor
import com.demircandemir.todoapp.ui.theme.taskItemTextColor
import com.demircandemir.todoapp.util.Action
import com.demircandemir.todoapp.util.RequestState
import com.demircandemir.todoapp.util.SearchAppBarState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun ListContent(
    allTasks: RequestState<List<Task>>,
    lowPriorityTasks: List<Task>,
    highPriorityTasks: List<Task>,
    sortState: RequestState<Priority>,
    searchedTasks: RequestState<List<Task>>,
    searchAppBarState: SearchAppBarState,
    onSwipeDelete: (Action, Task) -> Unit,
    navigateToTaskScreen: (taskId: Int) -> Unit
) {
    if (sortState is RequestState.Success){
        when {
            searchAppBarState == SearchAppBarState.TRİGGERED -> {
                if (searchedTasks is RequestState.Success) {
                    HandleListContent(
                        tasks = searchedTasks.data,
                        onSwipeDelete = onSwipeDelete,
                        navigateToTaskScreen = navigateToTaskScreen
                    )
                }
            }
            sortState.data == Priority.None -> {
                if (allTasks is RequestState.Success) {
                    HandleListContent(
                        tasks = allTasks.data,
                        onSwipeDelete = onSwipeDelete,
                        navigateToTaskScreen = navigateToTaskScreen
                    )
                }
            }
            sortState.data == Priority.Low -> {

               /* for (i in lowPriorityTasks) {
                    Log.d("sortState", "lowTasks: $i")
                }*/

                Log.d("sortState", "low")
                if (allTasks is RequestState.Success) {
                    Log.d("sortState", "sortListSuccess")
                    HandleListContent(
                        tasks = lowPriorityTasks,
                        onSwipeDelete = onSwipeDelete,
                        navigateToTaskScreen = navigateToTaskScreen
                    )
                }
            }
            sortState.data == Priority.High -> {
                    Log.d("sortState", "high")
                if (allTasks is RequestState.Success) {
                    HandleListContent(
                        tasks = highPriorityTasks,
                        onSwipeDelete = onSwipeDelete,
                        navigateToTaskScreen = navigateToTaskScreen
                    )
                }
            }


    }

    }

}

@ExperimentalMaterialApi
@Composable
fun HandleListContent(
    tasks: List<Task>,
    onSwipeDelete: (Action, Task) -> Unit,
    navigateToTaskScreen: (taskId: Int) -> Unit
) {
    if (tasks.isEmpty()) {
        EmptyContent()
    } else {
        /*for (i in tasks) {
            Log.d("sortState", "lowTasks(handleList): $i")
        }*/
        DisplayTasks(
            tasks = tasks,
            onSwipeDelete = onSwipeDelete,
            navigateToTaskScreen = navigateToTaskScreen
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@ExperimentalMaterialApi
@Composable
fun DisplayTasks(
    tasks: List<Task>,
    onSwipeDelete: (Action, Task) -> Unit,
    navigateToTaskScreen: (taskId: Int) -> Unit
) {

   /* var k = 0
    for (i in tasks) {
        Log.d("sortState", "lowTasks(displayTasks): $i")
        k++
    }

    Log.d("sortState", "$k")*/


    LazyColumn {
        items(
            items = tasks,
            key = { task ->
                task.id
            }
        ) { task ->

            val dismissState = rememberDismissState()
            val dismissDirection = dismissState.dismissDirection
            val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)
            if (isDismissed && dismissDirection== DismissDirection.EndToStart) {
                val scope = rememberCoroutineScope()
                scope.launch {
                    delay(300)
                    onSwipeDelete(Action.DELETE, task)
                }
            }



            val degrees by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default)
                    0f
                else
                    -45f
            )


            var itemAppeared by remember { mutableStateOf(false) }
            LaunchedEffect(key1 = true) {
                itemAppeared = true
            }

            AnimatedVisibility(
                visible = itemAppeared && !isDismissed,
                enter = expandVertically(
                    animationSpec = tween(
                        durationMillis = 300
                    )
                ),
                exit = shrinkHorizontally(
                    animationSpec = tween(
                        durationMillis = 300
                    )
                )
            ) {
                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    dismissThresholds = { FractionalThreshold(fraction = 0.2f) },
                    background = { RedBackground(degrees = degrees) },
                    dismissContent = {
                        TaskItem(
                            toDoTask = task,
                            navigateToTaskScreen = navigateToTaskScreen
                        )
                    }
                )
            }

            

        }
    }
}



@Composable
fun RedBackground(degrees: Float) {
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(HighPriorityColor)
            .padding(horizontal = LARGE_PADDING),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(modifier = Modifier.rotate(degrees = degrees),
            imageVector =Icons.Filled.Delete,
            contentDescription = stringResource(id = R.string.delete_task),
            tint = Color.White
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun TaskItem(
    toDoTask: Task,
    navigateToTaskScreen: (taskId : Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape,
        color = MaterialTheme.colors.taskItemBackgroundColor,
        elevation = TASK_ITEM_ELEVATİON,
        onClick = {
            navigateToTaskScreen(toDoTask.id)
        }
    ) {
        Column (
            Modifier
                .padding(all = LARGE_PADDING)
                .fillMaxWidth()
        ) {
            Row {
                Text(
                    modifier = Modifier
                        .weight(8f),
                    text = toDoTask.title,
                    color = MaterialTheme.colors.taskItemTextColor,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Canvas(
                        modifier =Modifier
                            .size(PRIORITY_INDICATOR_SIZE)
                    ) {
                        drawCircle(
                            color = toDoTask.priority.color
                        )
                    }
                }
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = toDoTask.description,
                color = MaterialTheme.colors.taskItemTextColor,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
@Preview
fun TaskItemPreview() {
    TaskItem(
        toDoTask = Task(
            id = 0,
            title = "Title",
            description = "Some random text",
            priority = Priority.High
        ),
        navigateToTaskScreen = {}
    )
}