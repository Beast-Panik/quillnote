package org.qosp.notes.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.qosp.notes.BuildConfig
import org.qosp.notes.components.MediaStorageManager
import org.qosp.notes.components.backup.BackupManager
import org.qosp.notes.data.repo.*
import org.qosp.notes.ui.reminders.ReminderManager
import javax.inject.Singleton

const val TEST_MEDIA_FOLDER = "test_media"

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [UtilModule::class],
)
object TestUtilModule {
    @Provides
    @Singleton
    fun provideMediaStorageManager(
        @ApplicationContext context: Context,
        noteRepository: NoteRepository,
    ) = MediaStorageManager(context, noteRepository, TEST_MEDIA_FOLDER)

    @Provides
    @Singleton
    fun provideReminderManager(
        @ApplicationContext context: Context,
        reminderRepository: ReminderRepository,
    ) = ReminderManager(context, reminderRepository)

    @Provides
    @Singleton
    fun provideBackupManager(
        noteRepository: NoteRepository,
        notebookRepository: NotebookRepository,
        tagRepository: TagRepository,
        reminderRepository: ReminderRepository,
        idMappingRepository: IdMappingRepository,
        reminderManager: ReminderManager,
        @ApplicationContext context: Context,
    ) = BackupManager(
        BuildConfig.VERSION_CODE,
        noteRepository,
        notebookRepository,
        tagRepository,
        reminderRepository,
        idMappingRepository,
        reminderManager,
        context
    )
}
