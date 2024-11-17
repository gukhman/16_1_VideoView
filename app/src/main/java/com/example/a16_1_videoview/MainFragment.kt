package com.example.a16_1_videoview

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.a16_1_videoview.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    // Привязка для доступа к элементам UI
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    // Лаунчер для выбора видео
    private lateinit var pickVideoLauncher: ActivityResultLauncher<Array<String>>

    // Детектор жестов для обработки двойных кликов
    private lateinit var gestureDetector: GestureDetector

    private var isFullscreen: Boolean = false
    private var videoUri: Uri? = null
    private var currentPosition: Int = 0 // Текущая позиция воспроизведения видео

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Если есть savedInstanceState, то применим его
        savedInstanceState?.let {
            videoUri = it.getParcelable("videoUri") // URI видео
            currentPosition = it.getInt("currentPosition", 0) // текущая позиция
        }

        // лаунчер для выбора видео
        pickVideoLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri?.let {
                videoUri = it
                playVideo(it, 0)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoView)

        // связываем контроллер и VideoView
        binding.videoView.setMediaController(mediaController)
        binding.videoView.requestFocus()

        // выбор видео
        binding.buttonSelectVideo.setOnClickListener {
            selectVideo()
        }

        // одиночный клик покажет mediaController
        binding.videoView.setOnClickListener {
            videoUri?.let {
                mediaController.show()
            }
        }

        // двойной клик через жесты
        gestureDetector =
            GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    toggleFullscreen() // Переключение полноэкранного режима при двойном клике
                    return true
                }
            })

        //  обработки жестов на videoView
        binding.videoView.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event) // Обработка жестов
            v.performClick() // Выполнение клика
            true
        }

        // Восстановление видео и его воспроизведение после поворота экрана
        videoUri?.let {
            playVideo(it, currentPosition)
        }
    }

    // Метод для выбора видео
    private fun selectVideo() {
        pickVideoLauncher.launch(arrayOf("video/*")) // Открытие диалога выбора видео
    }

    // Метод для воспроизведения видео
    private fun playVideo(uri: Uri, position: Int) {
        binding.videoView.setVideoURI(uri)
        if (position > 0) binding.videoView.seekTo(position)
        binding.videoView.start()
    }

    // Метод для включения/выключения полноэкранного режима
    private fun toggleFullscreen() {
        isFullscreen = !isFullscreen // Переключаем флаг полноэкранного режима
        if (isFullscreen) {
            // Включение полноэкранного режима
            requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            requireActivity().actionBar?.hide() // Скрываем ActionBar
            binding.buttonSelectVideo.visibility = View.GONE // Скрываем кнопку выбора видео
            binding.videoView.layoutParams.height =
                ViewGroup.LayoutParams.MATCH_PARENT // Растягиваем VideoView на весь экран
        } else {
            // Возвращение в обычный режим
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            requireActivity().actionBar?.show() // Показываем ActionBar
            binding.buttonSelectVideo.visibility = View.VISIBLE // Показываем кнопку выбора видео
            binding.videoView.layoutParams.height =
                ViewGroup.LayoutParams.WRAP_CONTENT // Устанавливаем стандартный размер для VideoView
        }
        binding.videoView.requestLayout() // Перерисовываем VideoView для применения изменений
    }

    // Сохранение состояния (URI видео и текущая позиция) при повороте экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("videoUri", videoUri) // Сохраняем URI выбранного видео
        outState.putInt(
            "currentPosition",
            binding.videoView.currentPosition
        ) // Сохраняем текущую позицию воспроизведения
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Освобождаем привязку, чтобы избежать утечек памяти
    }
}
