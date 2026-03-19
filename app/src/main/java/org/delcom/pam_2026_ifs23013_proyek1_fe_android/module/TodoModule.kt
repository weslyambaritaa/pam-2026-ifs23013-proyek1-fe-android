package org.delcom.pam_2026_ifs23013_proyek1_fe_android.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.todos.service.ITodoAppContainer
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.todos.service.ITodoRepository
import org.delcom.pam_2026_ifs23013_proyek1_fe_android.network.todos.service.TodoAppContainer

@Module
@InstallIn(SingletonComponent::class)
object TodoModule {
    @Provides
    fun providePlantContainer(): ITodoAppContainer {
        return TodoAppContainer()
    }

    @Provides
    fun providePlantRepository(container: ITodoAppContainer): ITodoRepository {
        return container.repository
    }
}