package com.example.hugo.contadordeestoque

import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import com.example.hugo.lessapedidos.GetScreenMetrics
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onFocusChange
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import java.util.*

/**
* Created by hugo on 26/10/17.
*/
class MainActivityUI : AnkoComponent<MainActivity> {
    private lateinit var nomeProduto : AutoCompleteTextView
    private lateinit var quantidade : EditText
    private val quantidadesProdutos = Hashtable<String,Int>()
    private var unidadesProdutos = Hashtable<String,String>()
    private lateinit var adapterListaProdutos : ArrayAdapter<String>
    private lateinit var lista : ListView
    private lateinit var fotoTirada : ImageView
    private lateinit var textoCodigo : TextView
    private lateinit var todosProdutos : ArrayAdapter<String>
    private lateinit var unidade : TextView
    val setFotoTirada = { bitmap : Bitmap ->
        fotoTirada.imageBitmap = bitmap
    }

    val setValorDecodificado = { valor: String ->
        textoCodigo.text = valor
    }
    val setTodosProdutos = { lista : ArrayList<String>->
        todosProdutos.addAll(lista)
        todosProdutos.notifyDataSetChanged()
    }

    val setProdutosUnidades = { unidades: Hashtable<String,String> ->
        this.unidadesProdutos = unidades
    }
    override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {
        adapterListaProdutos = ArrayAdapter(ui.ctx, R.layout.list_layout)
        todosProdutos = ArrayAdapter(ui.ctx, R.layout.list_layout)
        verticalLayout {
            val fab = FloatingActionButton(ui.ctx)
            val imagemVazio = imageView {
                imageResource = R.drawable.question_mark
            }.lparams {
                width = GetScreenMetrics(resources.displayMetrics.density).getLogoSize()
                height = GetScreenMetrics(resources.displayMetrics.density).getLogoSize()
                gravity = Gravity.CENTER_HORIZONTAL
            }
            val textoVazio1 = textView {
                textResource = R.string.lista_vazio
                gravity = Gravity.CENTER_HORIZONTAL
                textSize = GetScreenMetrics(resources.displayMetrics.density).getFontSizeDialogText()
            }
            val textoVazio2 = textView {
                textResource = R.string.lista_vazio_msg
                gravity = Gravity.CENTER_HORIZONTAL
                textSize = GetScreenMetrics(resources.displayMetrics.density).getFontSizeDialogText()
            }
            lista = listView {
                adapter = adapterListaProdutos
                visibility = View.GONE
                onItemClick { _, view, _, _ ->
                    view as TextView
                    alert(R.string.contagem_dialog_title) {
                        customView {
                            verticalLayout {
                                linearLayout {
                                    orientation = LinearLayout.HORIZONTAL
                                    textView {
                                        textResource = R.string.nome_produto_contado_label
                                    }
                                    textView {
                                        text = view.text
                                    }
                                }
                                linearLayout {
                                    orientation = LinearLayout.HORIZONTAL
                                    textView {
                                        textResource = R.string.qtd_produto_contado_label
                                    }
                                    textView {
                                        text = quantidadesProdutos[view.text.toString()].toString()
                                    }
                                }
                            }
                        }
                        okButton {  }
                        negativeButton(R.string.del_btn){
                            alert(R.string.remover_aviso_title) {
                                messageResource = R.string.remover_aviso_msg
                                yesButton {
                                    adapterListaProdutos.remove(view.text.toString())
                                    adapterListaProdutos.notifyDataSetChanged()
                                    quantidadesProdutos.remove(view.text.toString())
                                }
                                noButton {  }
                            }.show()
                        }
                    }.show()
                }
            }.lparams {
                height = dip(400)
                width = matchParent
            }.applyRecursively { view ->
                view.layout(dip(5),dip(5),0,0)
            }
            addView(fab)
            val lp = LinearLayout.LayoutParams(dip(50), dip(50))
            lp.gravity = Gravity.END
            lp.topMargin = -dip(10) + dip(200)
            lp.rightMargin = dip(10)
            fab.layoutParams = lp
            fab.scaleType = ImageView.ScaleType.CENTER
            fab.image = ui.ctx.getDrawable(R.drawable.ic_add_black_24dp)
            fab.size = FloatingActionButton.SIZE_NORMAL
            fab.onClick {
                alert(R.string.dados_produto_dialog_title){
                    customView {
                        verticalLayout {
                            nomeProduto = autoCompleteTextView {
                                setAdapter(todosProdutos)
                                hintResource = R.string.nome_produto_hint
                                onFocusChange { v, hasFocus ->
                                    if(!hasFocus)
                                    {
                                        unidade.text = unidadesProdutos[text.toString()]
                                    }
                                }
                            }
                            quantidade = editText{
                                inputType = InputType.TYPE_CLASS_NUMBER
                                hintResource = R.string.qtd_produto_hint
                            }
                            linearLayout {
                                orientation = LinearLayout.HORIZONTAL
                                textView {
                                    textResource = R.string.und_prod
                                    textSize = GetScreenMetrics(resources.displayMetrics.density).getFontSizeDialogText()
                                }
                                unidade = textView {
                                    textSize = GetScreenMetrics(resources.displayMetrics.density).getFontSizeDialogText()
                                    textResource = R.string.und_valor_padrao
                                }
                            }
                            linearLayout {
                                orientation = LinearLayout.HORIZONTAL
                                textView {
                                    textResource = R.string.tirar_foto_label
                                    textSize = GetScreenMetrics(resources.displayMetrics.density).getFontSizeDialogText()
                                }.lparams {
                                    gravity = Gravity.CENTER_VERTICAL
                                }
                                imageButton {
                                    imageResource = R.drawable.ic_camera_alt_black_24dp
                                    onClick {
                                        alert(R.string.codigo_foto){
                                            customView {
                                                verticalLayout {
                                                    val intentPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                                    if(intentPicture.resolveActivity(ui.ctx.packageManager)!= null)
                                                    {
                                                        startActivityForResult(ui.owner, intentPicture, 1, null)
                                                    }
                                                    fotoTirada = imageView {}
                                                    textoCodigo = textView {
                                                        textSize = GetScreenMetrics(resources.displayMetrics.density).getFontSizeDialogText()
                                                    }
                                                }
                                            }
                                            okButton {  }
                                        }.show()
                                    }
                                }.lparams{
                                    setMargins(dip(60),0,0,0)
                                    gravity = Gravity.END
                                }
                            }

                        }
                    }
                    yesButton {
                        if(adapterListaProdutos.getPosition(nomeProduto.text.toString()) == -1)
                        {
                            if(todosProdutos.getPosition(nomeProduto.text.toString()) == -1)
                            {
                                alert(R.string.prod_nao_cad_title)
                                {
                                    messageResource = R.string.prod_nao_cad_msg
                                    yesButton {
                                        adapterListaProdutos.add(nomeProduto.text.toString())
                                        adapterListaProdutos.notifyDataSetChanged()
                                        quantidadesProdutos.put(nomeProduto.text.toString(),quantidade.text.toString().toInt())
                                    }
                                    noButton {  }
                                }.show()
                            }
                            else
                            {
                                adapterListaProdutos.add(nomeProduto.text.toString())
                                adapterListaProdutos.notifyDataSetChanged()
                                quantidadesProdutos.put(nomeProduto.text.toString(),quantidade.text.toString().toInt())
                            }
                        }
                        else
                        {
                            toast(R.string.produto_ja_add_aviso)
                        }
                        if(lista.visibility == View.GONE)
                        {
                            lista.visibility = View.VISIBLE
                            imagemVazio.visibility = View.GONE
                            textoVazio1.visibility = View.GONE
                            textoVazio2.visibility = View.GONE
                            lp.topMargin = -dip(10)
                            fab.layoutParams = lp
                        }
                    }
                    noButton {  }
                }.show()
            }
        }
    }.view
}