const electron = require('electron')
const app = electron.app
const BrowserWindow = electron.BrowserWindow

const path = require('path')
const url = require('url')

let mainWindow

const createWindow = () => {
  mainWindow = new BrowserWindow({width: 1200, height: 600})

  mainWindow.loadURL(url.format({
    pathname: path.join(__dirname, 'public', 'home.html'),
    protocol: 'file:',
    slashes: true
  }))

  //mainWindow.webContents.openDevTools()

  mainWindow.on('closed', () => {
    mainWindow = null
  })
}

app.on('ready', createWindow)

app.on('window-all-closed', () => {
  app.quit()
})

app.on('activate', () => {
  if (mainWindow === null) {
    createWindow()
  }
})
