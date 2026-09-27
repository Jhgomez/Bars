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
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.appBarWithSearchColors
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.material3.rememberSearchBarScrollState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberSearchBarWithGapState
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

/******************** Search Bar ***********************/
//                SearchBar ----------- Baseline Implementation
//                AppBarWithSearch ---- Builds on top of SearchBar but enables adding scroll
//                                      behaviors and behaves more like a top TopAppBar since it
//                                      draws a surface around the search input and also enables
//                                      actions which is something that the previous implementation
//                                      didn't have and makes it feel more like a top app bar. This
//                                      implementation has a weird behavior, remember android
//                                      require apps to be edge-to-edge, and as mentioned, this
//                                      search draws a surface around the search input, but it
//                                      doesn't draw the same color on the system's status bar;
//                                      which is something that a top app bar will do for us
//                                      automatically, and this also happens when the bar is being
//                                      scrolled back in by the nested scroll behavior, all this
//                                      together(scroll behavior, edge-to-edge viewport and the
//                                      exclusive coloring of search bar), causes something that
//                                      feels kind of weird at least to me, and that the material3
//                                      documentation doesn't seem to talk about, and it is that the
//                                      scrollable content that drives the nested scroll behavior
//                                      will be obscured by the search bar but not by status bar,
//                                      again, this doesn't happen when using collapsible top bars,
//                                      so I could try nesting the search bar inside an actual top
//                                      bar or color the status bar but I would need to make it
//                                      transparent when the scroll bar is scrolled out, or I could
//                                      ask to the material team somehow if this is the expected
//                                      behavior and just let it be.

/******************** **********************/
//                All below three functions create a SearchBarState, it lets us control programmatically
//                if the search view is shown or not, also recompose if its state is collapsed(when
//                the search view, meaning the results list, is not visible) or not and show some
//                icon or not based on its current state as in this example

//                rememberSearchBarState()
//                rememberContainedSearchBarState()
//                rememberSearchBarWithGapState()

/******************** Search view for Small screens **********************/
//                ExpandedFullScreenSearchBar ------------ Divided Style
//                ExpandedFullScreenContainedSearchBar --- Contained Style


/******************** Search view for Medium and large screens **********************/
//                ExpandedDockedSearchBar ---------------- Divided Style
//                ExpandedDockedSearchBarWithGap --------- Contained Style
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DockedSearchBar() {
    val textFieldState = rememberTextFieldState()
    val dockedSearchBarState = rememberSearchBarState()
    val searchBarScrollBehaviorScrollState = rememberSearchBarScrollState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(
        scrollState = searchBarScrollBehaviorScrollState
    )
    val appBarWithSearchColors = appBarWithSearchColors(
        searchBarColors = SearchBarColors(
            containerColor = Color(0xFFEFB8C8),  // the color of the search input container(meaning, not the area visibly around the input field)
            dividerColor = Color.Red,            // the color of the divider between the input field and the search results(divided style only)
            inputFieldColors = textFieldColors() // colors applied to the input field
        ),
        scrolledSearchBarContainerColor = Color(0xFFF4511E), // color of the text input when scrolling back in by nested scroll to color it differently before it scrolls out set SearchBarColors.containerColor in the searchBarColors param
        appBarContainerColor = Color(0xFF3949AB), // surface around the input text field when not scrolled out by nested scroll
        scrolledAppBarContainerColor = Color(0xFFFDD835), // surface around the input text field when scrolling back in by nested scroll
        appBarNavigationIconColor = Color(0xFFA5D6A7),
        appBarActionIconColor = Color(0xFFBA68C8)
    )

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = dockedSearchBarState,
                // If I set it up from here it seems like its color wont be driven by the SearchBarState and colors assigned to AppBarWithSearch
//                colors = appBarWithSearchColors.searchBarColors.inputFieldColors, // or  inputFieldColors() or TextFieldDefaults.colors
                onSearch = { scope.launch { dockedSearchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search")
                },
                leadingIcon = { LeadingIcon(dockedSearchBarState, scope) },
                trailingIcon = { TrailingIcon() },
            )
        }

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
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
//                modifier = Modifier.fillMaxSize(), // you may want to use the default size here
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
                    clippingEnabled = false, // this one is very important, if true, we could get a weird behavior
                    usePlatformDefaultWidth = false, // default with could limit your list size so most likely you want it to be false
                    excludeFromSystemGesture = true,
                    windowType = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
                    windowToken = null, // IBinder type
                    blurBehindRadius = 8.dp,
                    scrimAlpha = .32f,
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