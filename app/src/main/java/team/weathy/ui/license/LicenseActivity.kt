package team.weathy.ui.license

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import team.weathy.databinding.ActivityLicenseBinding
import team.weathy.util.setOnDebounceClickListener

class LicenseActivity : AppCompatActivity() {

    lateinit var licenseAdapter: LicenseAdpater
    val data = mutableListOf<LicenseData>()
    private lateinit var binding: ActivityLicenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.exitLicenseBtn.setOnDebounceClickListener {
            finish()
        }

        binding.rvLicense.addItemDecoration(LicenseDecoration(18))
        licenseAdapter = LicenseAdpater(this)
        binding.rvLicense.adapter = licenseAdapter
        loadData()

        licenseAdapter.setItemClickListener(object : LicenseAdpater.ItemClickListener {
            override fun onClick(view: View, position: Int) {
                val intent = Intent(this@LicenseActivity, LicenseDetailActivity::class.java)
                intent.putExtra("title", data[position].title)
                intent.putExtra("content", data[position].content)
                startActivity(intent)
            }
        })
    }

    private fun loadData() {
        data.apply {
            add(
                LicenseData(
                    title = "Kotlin",
                    content = "Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.\n" +
                            "\n" +
                            "   Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            "   you may not use this file except in compliance with the License.\n" +
                            "   You may obtain a copy of the License at\n" +
                            "\n" +
                            "       http://www.apache.org/licenses/LICENSE-2.0\n" +
                            "\n" +
                            "   Unless required by applicable law or agreed to in writing, software\n" +
                            "   distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            "   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            "   See the License for the specific language governing permissions and\n" +
                            "   limitations under the License."
                )
            )
            add(
                LicenseData(
                    title = "Glide",
                    content = "Copyright 2014 Google, Inc. All rights reserved.\n" +
                            "\n" +
                            "Redistribution and use in source and binary forms, with or without modification, are\n" +
                            "permitted provided that the following conditions are met:\n" +
                            "\n" +
                            "   1. Redistributions of source code must retain the above copyright notice, this list of\n" +
                            "         conditions and the following disclaimer.\n" +
                            "\n" +
                            "   2. Redistributions in binary form must reproduce the above copyright notice, this list\n" +
                            "         of conditions and the following disclaimer in the documentation and/or other materials\n" +
                            "         provided with the distribution.\n" +
                            "\n" +
                            "THIS SOFTWARE IS PROVIDED BY GOOGLE, INC. ``AS IS'' AND ANY EXPRESS OR IMPLIED\n" +
                            "WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND\n" +
                            "FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GOOGLE, INC. OR\n" +
                            "CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR\n" +
                            "CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR\n" +
                            "SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON\n" +
                            "ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING\n" +
                            "NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF\n" +
                            "ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."
                )
            )
            add(
                LicenseData(
                    title = "Retrofit",
                    content = "Copyright 2013 Square, Inc.\n" +
                            "\n" +
                            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            "you may not use this file except in compliance with the License.\n" +
                            "You may obtain a copy of the License at\n" +
                            "\n" +
                            "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                            "\n" +
                            "Unless required by applicable law or agreed to in writing, software\n" +
                            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            "See the License for the specific language governing permissions and\n" +
                            "limitations under the License."
                )
            )
            licenseAdapter.data = data
            licenseAdapter.notifyDataSetChanged()
        }
    }
}