package red.man10.man10authentication

import net.alpenblock.bungeeperms.*
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class Man10Authentication : Plugin() ,Listener{

    var groupName = "user"
    val codeMap = HashMap<ProxiedPlayer,String>()

    override fun onEnable() {
        // Plugin startup logic

        proxy.pluginManager.registerListener(this,this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }


    @EventHandler
    fun userLogin(e:PostLoginEvent){
        val p = e.player

        if (BungeePermsAPI.userInGroup(p.uniqueId.toString(),groupName))return

        showAuthenticationMsg(p)
    }

    @EventHandler
    fun chat(e:ChatEvent){

        val p = e.sender
        if (p !is ProxiedPlayer)return

        if (BungeePermsAPI.userInGroup(p.uniqueId.toString(),groupName))return

        if (!checkCode(p,e.message)){
            showAuthenticationMsg(p)
            e.isCancelled = true
            return
        }

        addGroup(p)

    }

    fun showAuthenticationMsg(p:ProxiedPlayer){
        val code = String.format("%06d", Random().nextInt(1000000))

        codeMap[p] = code

        val text = TextComponent("以下の6桁の認証コードをチャット欄に入力してください")
        text.isBold = true
        text.color = ChatColor.GREEN
        text.isUnderlined = true
        val textEn = TextComponent("Type on chat 6 digit code displayed below.")
        text.isBold = true
        text.isUnderlined = true
        text.color = ChatColor.BLUE

        val codeText = TextComponent(code)
        codeText.isBold = true
        codeText.color = ChatColor.AQUA

        p.sendMessage(text)
        p.sendMessage(textEn)
        p.sendMessage(codeText)
    }

    fun checkCode(p:ProxiedPlayer,msg:String):Boolean{

        val correctCode = codeMap[p]?:return false

        if (correctCode == msg)return true

        return false
    }

    fun addGroup(p:ProxiedPlayer){
        BungeePermsAPI.userAddGroup(p.uniqueId.toString(),groupName)

        val text = TextComponent("認証完了！")
        text.isBold = true
        text.color = ChatColor.GREEN

        val textEn = TextComponent("Authentication Success!")
        text.isBold = true
        text.isUnderlined = true
        text.color = ChatColor.GREEN

        p.sendMessage(text)
        p.sendMessage(textEn)

    }

}