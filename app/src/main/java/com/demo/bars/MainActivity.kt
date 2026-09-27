package com.demo.bars

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedDockedSearchBarWithGap
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.appBarWithSearchColors
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.demo.bars.ui.theme.BarsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarsTheme {
                DockedSearchBar()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DockedSearchBar() {
    /********************  Small screens **********************/
//                ExpandedFullScreenContainedSearchBar() { }


    /********************  Medium and large screens **********************/
//                ExpandedDockedSearchBar(
//
//                )

    val textFieldState = rememberTextFieldState()
    val dockedSearchBarState = rememberContainedSearchBarState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val appBarWithSearchColors = appBarWithSearchColors()

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = dockedSearchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors, // or  inputFieldColors() or TextFieldDefaults.colors
                onSearch = { scope.launch { dockedSearchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search")
                },
                leadingIcon = { LeadingIcon(dockedSearchBarState, scope) },
                trailingIcon = { TrailingIcon() },
            )
        }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBarWithSearch(
                scrollBehavior = scrollBehavior,
                state = dockedSearchBarState,
                colors = appBarWithSearchColors,
                inputField = inputField,
                navigationIcon = { NavigationIcon(dockedSearchBarState) },
                actions = { SampleActions(dockedSearchBarState) },
            )

            // ExpandedDockedSearchBar
            ExpandedDockedSearchBarWithGap(
                state = dockedSearchBarState,
                inputField = inputField,
                modifier = Modifier.fillMaxSize(),
                shape = MaterialTheme.shapes.extraLarge,
                dropdownShape = MaterialTheme.shapes.medium,
                dropdownGapSize = 8.dp,
                dropdownScrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
                colors = appBarWithSearchColors.searchBarColors, // or SearchBarDefaults.colors() or SearchBarColors
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    clippingEnabled = true, // false
                    usePlatformDefaultWidth = true,
                    excludeFromSystemGesture = true,
                    windowType = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
                    windowToken = null, // IBinder type
                    blurBehindRadius = 16.dp,
                    scrimAlpha = 50f,
                )
            ) {
                SearchResults(
                    onResultClick = { result ->
                        textFieldState.setTextAndPlaceCursorAtEnd(result)
                        scope.launch { dockedSearchBarState.animateToCollapsed() }
                    }
                )
            }
        }
    ) { padding ->

        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val list = List(100) { "Text $it" }
            items(count = list.size) {
                Text(
                    text = list[it],
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun SearchResults(onResultClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        repeat(10) { idx ->
            val resultText = "Suggestion $idx"
            ListItem(
                content = { Text(resultText) },
                supportingContent = { Text("Additional info") },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.star),
                        contentDescription = null
                    )
                 },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier =
                    Modifier.clickable { onResultClick(resultText) }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SampleActions(state: SearchBarState, isAnimated: Boolean = false) =
    AnimatedVisibility(
        visible = !isAnimated || state.targetValue == SearchBarValue.Collapsed,
        enter =
            slideIn(
                animationSpec = motionScheme.fastSpatialSpec(),
                initialOffset = { IntOffset(it.width, 0) },
            ),
        exit =
            slideOut(
                animationSpec = tween(durationMillis = 150, delayMillis = 0),
                targetOffset = { IntOffset(it.width, 0) },
            ),
    ) {
        TooltipBox(
            positionProvider =
                TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
            tooltip = {
                PlainTooltip(
                    modifier =
                        Modifier.semantics {
                            // TODO(b/496338253): Remove this modifier once bug where tooltip text
                            //  is not announced by a11y screen readers is resolved.
                            liveRegion = LiveRegionMode.Assertive
                            paneTitle = "Account"
                        }
                ) {
                    Text("Account")
                }
            },
            state = rememberTooltipState(),
        ) {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.account_circle),
                    contentDescription = "Account"
                )
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationIcon(state: SearchBarState, isAnimated: Boolean = false) =
    AnimatedVisibility(
        visible = !isAnimated || state.targetValue == SearchBarValue.Collapsed,
        enter =
            slideIn(
                animationSpec = motionScheme.fastSpatialSpec(),
                initialOffset = { IntOffset(-it.width, 0) },
            ),
        exit =
            slideOut(
                animationSpec = tween(durationMillis = 150, delayMillis = 0),
                targetOffset = { IntOffset(-it.width, 0) },
            ),
    ) {
        TooltipBox(
            positionProvider =
                TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
            tooltip = {
                PlainTooltip(
                    modifier =
                        Modifier.semantics {
                            // TODO(b/496338253): Remove this modifier once bug where tooltip text
                            //  is not announced by a11y screen readers is resolved.
                            liveRegion = LiveRegionMode.Assertive
                            paneTitle = "Menu"
                        }
                ) {
                    Text("Menu")
                }
            },
            state = rememberTooltipState(),
        ) {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.menu),
                    contentDescription = "Menu"
                )
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeadingIcon(searchBarState: SearchBarState, scope: CoroutineScope) =
    if (searchBarState.currentValue == SearchBarValue.Expanded) {
        TooltipBox(
            positionProvider =
                TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
            tooltip = {
                PlainTooltip(
                    modifier =
                        Modifier.semantics {
                            // TODO(b/496338253): Remove this modifier once bug where tooltip text
                            //  is not announced by a11y screen readers is resolved.
                            liveRegion = LiveRegionMode.Assertive
                            paneTitle = "Back"
                        }
                ) {
                    Text("Back")
                }
            },
            state = rememberTooltipState(),
        ) {
            IconButton(onClick = { scope.launch { searchBarState.animateToCollapsed() } }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.arrow_back),
                    contentDescription = "Back"
                )
            }
        }
    } else {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.search),
            contentDescription = null
        )
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrailingIcon() =
    TooltipBox(
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip(
                modifier =
                    Modifier.semantics {
                        // TODO(b/496338253): Remove this modifier once bug where tooltip text is
                        //  not announced by a11y screen readers is resolved.
                        liveRegion = LiveRegionMode.Assertive
                        paneTitle = "Mic"
                    }
            ) {
                Text("Mic")
            }
        },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = { /* doSomething() */ }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.add),
                contentDescription = "Add"
            )
        }
    }