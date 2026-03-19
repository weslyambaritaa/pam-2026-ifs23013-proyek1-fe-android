package org.delcom.pam_proyek1_ifs23013.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.delcom.pam_proyek1_ifs23013.network.foods.service.IFoodAppContainer
import org.delcom.pam_proyek1_ifs23013.network.foods.service.IFoodRepository
import org.delcom.pam_proyek1_ifs23013.network.foods.service.FoodAppContainer

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