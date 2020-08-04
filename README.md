# pxp_ui_android

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)
# Requerimientos 
##### AndroidStudio 3.6.3 o superior

### Personalización
- dentro de /pxp-ui-android/app/src/main/res/values en el archivo colors.xml reemplazar los valores por los requeridos para el proyecto.
- Los valores de las cadenas en diferentes idiomas estan dentro de la carpeta /pxp-ui-android/app/src/main/res/values-xx/strings.xml con el codigo del idioma correspondiente, 
- dentro de /pxp-ui-android/app/src/main/res/mipmap-xxx se encuentra el recurso del logo en los tamaños correspondientes.
- dentro de /pxp-ui-android/app/src/main/java/com/kplian/pxpui/Utils/Constants.java se encuentran las constantes de conexión 
### FireBase
- reemplazar el arvhivo google-services.json con los datos de configuracion firebease adecuado para el proyecto usando el nombre de paquete adecuado correspondiente.

### Google
- En el archivo /pxp-ui-android/app/src/main/res/values/strings.xml reemplazar el atributo google_client_id por el nuevo Id generado para el proyecyo.

### Facebook
- En el archivo /pxp-ui-android/app/src/main/res/values/strings.xml reemplazar el atributo facebook_app_id por el nuevo Id generado para el proyecyo, tambien el atributo fb_login_protocol_scheme.
