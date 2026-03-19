package org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.screens.foods

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.helper.*
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.data.ResponseFoodData
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.components.*
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.ui.viewmodels.*
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.R

@Composable
fun FoodsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {
    // ... [Pertahankan deklarasi state yang sudah Anda buat di file asli] ...
    val uiStateAuth by authViewModel.uiState.collectAsState()
    val uiStateFood by foodViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var foods by remember { mutableStateOf<List<ResponseFoodData>>(emptyList()) }
    var authToken by remember { mutableStateOf<String?>(null) }

    // STATE FILTER
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filterOptions = listOf("Semua", "Makanan", "Minuman")

    fun fetchFoodsData() {
        isLoading = true
        authToken = (uiStateAuth.auth as AuthUIState.Success).data.authToken
        foodViewModel.getAllFoods(authToken ?: "", searchQuery.text)
    }

    LaunchedEffect(Unit) {
        isLoading = true
        if (uiStateAuth.auth !is AuthUIState.Success) {
            RouteHelper.to(navController, ConstHelper.RouteNames.Home.path, true)
            return@LaunchedEffect
        }
        fetchFoodsData()
    }

    LaunchedEffect(uiStateFood.foods) {
        if (uiStateFood.foods !is FoodsUIState.Loading) {
            isLoading = false
            foods = if (uiStateFood.foods is FoodsUIState.Success)
                (uiStateFood.foods as FoodsUIState.Success).data
            else emptyList()
        }
    }

    // Filter Logic Lokal
    val filteredFoods = if (selectedFilter == "Semua") {
        foods
    } else {
        foods.filter { it.category.equals(selectedFilter, ignoreCase = true) }
    }

    // ... [Bagian menu logout dsb persis sama] ...

    if (isLoading) { LoadingUI(); return }

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        TopAppBarComponent(
            navController = navController, title = "Foods", showBackButton = false,
            withSearch = true, searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }, onSearchAction = { fetchFoodsData() }
        )

        // FILTER ROW CHIPS
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { option ->
                FilterChip(
                    selected = selectedFilter == option,
                    onClick = { selectedFilter = option },
                    label = { Text(option) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            FoodsUI(
                foods = filteredFoods,
                onOpen = { RouteHelper.to(navController, "foods/$it") },
                onLoadMore = {
                    // Panggil fungsi ViewModel loadNextPage() disini jika API mendukung
                    // foodViewModel.loadMoreFoods(...)
                }
            )

            FloatingActionButton(
                onClick = { RouteHelper.to(navController, ConstHelper.RouteNames.FoodsAdd.path) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah")
            }
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun FoodsUI(
    foods: List<ResponseFoodData>,
    onOpen: (String) -> Unit,
    onLoadMore: () -> Unit // <-- Endless Scroll Callback
) {
    if (foods.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Tidak ada data!") }
        return
    }

    val listState = rememberLazyListState()

    // DETEKSI INFINITE SCROLL
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex >= foods.size - 1) {
                    onLoadMore() // Load data tambahan saat user scroll ke paling bawah
                }
            }
    }

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(foods) { food ->
            FoodItemUI(food, onOpen)
        }
    }
}
// (FoodItemUI tetap sama seperti milik Anda)

@Composable
fun FoodItemUI(
    food: ResponseFoodData,
    onOpen: (String) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                onOpen(food.id)
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Row(
            modifier = Modifier.padding(12.dp)
        ) {

            AsyncImage(
                model = ToolsHelper.getFoodImage(
                    food.id,
                    food.updatedAt
                ),
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_placeholder),
                contentDescription = food.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = food.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rp ${food.price}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}