package org.delcom.pam_2026_ifs23013_proyek1_fe_android.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.service.IFoodAppContainer
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.service.IFoodRepository
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.foods.service.FoodAppContainer

@Module
@InstallIn(SingletonComponent::class)
object FoodModule {

    @Provides
    fun provideFoodContainer(): IFoodAppContainer {
        return FoodAppContainer()
    }

    @Provides
    fun provideFoodRepository(container: IFoodAppContainer): IFoodRepository {
        return container.repository
    }
}