package com.example.movieapp.presentation.movieWebPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.movieapp.core.convertToHttps
import com.example.movieapp.core.toSafeString
import com.example.movieapp.databinding.FragmentMovieWebPageBinding


class MovieWebPageFragment : Fragment() {

    private var _binding: FragmentMovieWebPageBinding? = null

    private val binding get() = _binding!!
    private val args:MovieWebPageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMovieWebPageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = args.url
        binding.webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadUrl(url.convertToHttps().toSafeString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}