package org.hon.log.analysis.search.loader

import groovy.swing.SwingBuilder
import javax.swing.*
import java.awt.*

class Test{
    static void main(String[] args){
        def test = new Test()
        test.show()
    }

    void show(){
        def swingBuilder = new SwingBuilder()

        def customMenuBar = {
            swingBuilder.menuBar{
                menu(text: "File", mnemonic: 'F') {
                    menuItem(text: "Exit", mnemonic: 'X', actionPerformed: { dispose() })
                }
            }
        }

        def searchPanel = {
            swingBuilder.panel(constraints: BorderLayout.NORTH){
                searchField = textField(columns:15)
                button(text:"Search", actionPerformed:{ /* TODO */ } )
            }
        }

        swingBuilder.frame(title:"Autosuggestions",
        defaultCloseOperation:JFrame.EXIT_ON_CLOSE,
        size:[400, 500],
        show:true) {  
        customMenuBar()
        searchPanel()  
        }
    }
}