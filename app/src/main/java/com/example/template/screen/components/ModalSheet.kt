package com.example.template.screen.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.template.R
import com.example.template.screen.maps.PlaceModelSheetState
import com.example.template.screen.maps.PlaceModelSheetUi
import kotlin.math.ceil
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    state: PlaceModelSheetUi,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier.fillMaxSize(),

        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        MarkerDetailSheet(state = state)
    }
}

@Composable
fun MarkerDetailSheet(state: PlaceModelSheetUi, modifier: Modifier = Modifier) {
    val placeModel = state.markerPlacesModel
    Column(
        modifier
            .fillMaxSize()
            .padding(15.dp),
    ) {
        when (state.placeModelSheetState) {
            PlaceModelSheetState.Loading -> {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }

            PlaceModelSheetState.ERROR -> {}
            PlaceModelSheetState.Loaded -> {
                val context = LocalContext.current
                Text(
                    text = placeModel!!.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${state.placeDetails?.result?.formattedAddress}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    placeModel.rating?.let { rating ->
                        Text(
                            text = rating.toString(),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        RatingBar(rating = rating)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "(${placeModel.userRatingsTotal})",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    val gmmIntentUri =
                        Uri.parse("google.navigation:q=" + state.markerPlacesModel?.geometry?.location?.lat + "," + state.markerPlacesModel?.geometry?.location?.lng + "&mode=b")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                }) {
                    Text(text = "Directions")
                }
                Spacer(modifier = Modifier.height(8.dp))
                PhotoList(modifier = Modifier, imageUrlList = state.imageList)
                Spacer(modifier = Modifier.height(4.dp))
                state.markerPlacesModel?.let {
                    Text(
                        text = "Business Status : ${it.businessStatus}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                state.markerPlacesModel?.let {
                    Text(
                        text = "Open Now : ${it.openingHours?.openNow}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                state.markerPlacesModel?.let {
                    Text(
                        text = "Plus Code : ${it.plusCode?.compoundCode}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Double = 0.0,
    stars: Int = 5,
    starsColor: Color = Color.Yellow,
) {
    val filledStars = floor(rating).toInt()
    val unfilledStars = (stars - ceil(rating)).toInt()
    val halfStar = !(rating.rem(1).equals(0.0))
    Row(modifier = modifier) {
        repeat(filledStars) {
            Icon(imageVector = Icons.Outlined.Star, contentDescription = null, tint = starsColor)
        }
        if (halfStar) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_star_half_24),
                contentDescription = null,
                tint = starsColor,
            )
        }
        repeat(unfilledStars) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_star_outline_24),
                contentDescription = null,
                tint = starsColor,
            )
        }
    }
}

@Composable
fun PhotoList(imageUrlList: List<String>, modifier: Modifier = Modifier) {
    val lazyListState = rememberLazyListState()
    LazyRow(
        state = lazyListState,
        horizontalArrangement = Arrangement.spacedBy(15.dp),

        ) {
        items(items = imageUrlList) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .height(300.dp)
                    .width(260.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = it)
                            .apply(block = fun ImageRequest.Builder.() {
                                crossfade(true)
                            }).build(),
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                )
            }
        }
    }
}
