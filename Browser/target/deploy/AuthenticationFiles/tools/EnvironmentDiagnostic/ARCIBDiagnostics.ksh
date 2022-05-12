#!/bin/ksh
# Read environment.vars and get all the required values for CALLJ Execution,4Tress API and Seccurity Policy Files

    echo             "                 System Information tool for ARC-IB T24 environment  Revision 1.0              "
    echo             "                ---------------------------------------------------------------------          "
    echo             "                                                                                               "
# Get the TCServer Location and store it in the get_tcserver variable
    echo "Please enter your tcserver path(Absolute Path) "
    read get_tcserver

# iniialise a variable with location of environment.vars  value
    env_rem="/conf/TCServer/environment.vars"

# Concatenate the absoulte path and location of environment.vars
    env_vars="$get_tcserver$env_rem"
    echo $env_vars

# open environment.vars and read ,count the total number of lines .If file does not exists tool exits
#if not we read the whole environment variables and store it in the env_array
    typeset -i jbc_count=0
    v_java_home=
    v_path=
    v_libpath=
    v_classpath=
    set -A v_jbcjvmopt

    set -A env_array
    if [[ -f $env_vars ]] ; then
    jbc_count=`grep -c "JBCJVMOPT" $env_vars`
    typeset -i nt1=0
    while read LINE1
    do
    env_array[$nt1]=$LINE1
    nt1=$nt1+1
    done < $env_vars

#varibales will store the position of java_home,path,libpath.. which will be used to extract the path alone.

    typeset -i nt2=0
    typeset -i jhome_pos=0
    typeset -i jpath_pos=0
    typeset -i jlibpath_pos=0
    typeset -i jclasspath_pos=0
    typeset -i jbcjvmopt1_pos=0
    typeset -i jbcjvmopt2_pos=0
    typeset -i jbcjvmopt3_pos=0
    typeset -i jbcjvmopt4_pos=0
    typeset -i jbcjvmopt5_pos=0

# While loop which will search for required environment variables and get the position and assign it to
# jhome_pos, jbcjvmopt1_pos etc

    while [[ $nt2 -le ${#env_array[*]} ]] ;do
    x1=${env_array[$nt2]}
    jhome=`expr substr "$x1" 1 9`
    jpath=`expr substr "$x1" 1 4`
    jlibpath=`expr substr "$x1" 1 7`
    jclasspath=`expr substr "$x1" 1 9`
    jbcjvmopt1=`expr substr "$x1" 1 10`
    jbcjvmopt2=`expr substr "$x1" 1 10`
    jbcjvmopt3=`expr substr "$x1" 1 10`
    jbcjvmopt4=`expr substr "$x1" 1 10`
    jbcjvmopt5=`expr substr "$x1" 1 10`

    if [[ $jhome = "JAVA_HOME" ]] ; then
    jhome_pos=$nt2
    fi
    if [[ $jpath = "PATH" ]] ; then
    jpath_pos=$nt2
    fi
    if [[ $jlibpath = "LIBPATH" ]] ; then
    jlibpath_pos=$nt2
    fi
    if [[ $jclasspath = "CLASSPATH" ]] ; then
    jclasspath_pos=$nt2
    fi
    if [[ $jbcjvmopt1 = "JBCJVMOPT1" ]] ; then
    jbcjvmopt1_pos=$nt2
    fi
    if [[ $jbcjvmopt2 = "JBCJVMOPT2" ]] ; then
    jbcjvmopt2_pos=$nt2
    fi
    if [[ $jbcjvmopt3 = "JBCJVMOPT3" ]] ; then
    jbcjvmopt3_pos=$nt2
    fi
    if [[ $jbcjvmopt4 = "JBCJVMOPT4" ]] ; then
    jbcjvmopt4_pos=$nt2
    fi
    if [[ $jbcjvmopt5 = "JBCJVMOPT5" ]] ; then
    jbcjvmopt5_pos=$nt2
    fi
    nt2=$nt2+1
    done

# Declare global varibales which will be used by this shell script

    v_java_home=${env_array[$jhome_pos]}
    v_path=${env_array[$jpath_pos]}
    v_libpath=${env_array[$jlibpath_pos]}
    v_classpath=${env_array[$jclasspath_pos]}
    v_jbcjvmopt[0]=${env_array[$jbcjvmopt1_pos]}
    v_jbcjvmopt[1]=${env_array[$jbcjvmopt2_pos]}
    v_jbcjvmopt[2]=${env_array[$jbcjvmopt3_pos]}
    v_jbcjvmopt[3]=${env_array[$jbcjvmopt4_pos]}
    v_jbcjvmopt[4]=${env_array[$jbcjvmopt5_pos]}

    else
    search_loc=$HOME/.profile
    jbc_count=`grep -c "JBCJVMOPT" $search_loc`
    echo "****************** WARNING ******************************** "
    echo " environment.vars does not exist in mentioned location "
    echo " Tool will get the value from .profile  "
    echo "*********************************************************** "
    v_java_home="JAVA_HOME=$JAVA_HOME"
    v_path="PATH=$PATH"
    v_libpath="LIBPATH=$LIBPATH"
    v_classpath="CLASSPATH=$CLASSPATH"
    v_jbcjvmopt[0]="JBCJVMOPT1=$JBCJVMOPT1"
    v_jbcjvmopt[1]="JBCJVMOPT2=$JBCJVMOPT2"
    v_jbcjvmopt[2]="JBCJVMOPT3=$JBCJVMOPT3"
    v_jbcjvmopt[3]="JBCJVMOPT4=$JBCJVMOPT4"
    v_jbcjvmopt[4]="JBCJVMOPT5=$JBCJVMOPT5"
    fi

# Extracting only the path to be checked for existence
    typeset -i eqpos1=11
    typeset -i str_len1=${#v_java_home}
    v_java_home_val=`expr substr "$v_java_home" "${eqpos1}" "${str_len1}"`


    typeset -i col_chk=1
    typeset -i col_pre=0
    while [[ $col_chk -le ${#v_java_home} ]] ; do
    col=`expr substr "$v_java_home_val" "$col_chk" 1`
    if [[ $col == ":" ]] ;then
    col_pre=$col
    fi
    col_chk=$col_chk+1
    done
    if [[ $col_pre = 0 ]] ; then
    path_jhome=$v_java_home_val
    fi

# Java_HOME directory exists
    typeset -i flag=0

    if [[ -d $path_jhome ]] ; then
    flag=1
    else
    echo " $path_jhome does not exists "
    fi

# Getting the version and bit information of Java

    typeset -i version=0
    typeset -i bit=0
    version=`$path_jhome/bin/java -version 2>&1 | grep 1.5.0 | wc -l`
    bit=`$path_jhome/bin/java -version 2>&1 | grep ppc64 | wc -l`
    if [[ $version -ge 1 && $bit -ge 1 ]] ; then
    flag=2
    else
    echo "  Install Java 1.5 64 bit and Check your path if you got older version of java path mentioned,just remove it "
    fi
    echo "                  "
    echo " JAVA ENVIRONMENT "
    echo " ================ "
    echo " JAVA_HOME         : $path_jhome "
    echo " JAVA VERSION      : 1.5.0       "


    set -A bin_dir
    bin_dir[0]=$path_jhome/bin
    bin_dir[1]=$path_jhome/jre/bin
    bin_dir[2]=$path_jhome/jre/bin/j9vm

    if [[ $flag = 2 ]] ; then
    typeset -i dir=0
    while [[ $dir -lt ${#bin_dir[*]} ]] ;do
    if [[ -d ${bin_dir[$dir]} ]] ;then
    echo " JAVA ENV$dir         : ${bin_dir[$dir]}  "
    else
    echo "${bin_dir[$dir]} does not exist in your environment ."
    fi
    dir=$dir+1
    done
    fi
    typeset -i eqpos2=6
    typeset -i str_len2=${#v_path}
    v_path_val=`expr substr "$v_path" "${eqpos2}" "${str_len2}"`

    typeset -i eqpos3=9
    typeset -i str_len3=${#v_libpath}
    v_lib_path=`expr substr "$v_libpath" "${eqpos3}" "${str_len3}"`
    set -A lib_dir
    lib_dir[0]=$path_jhome/jre/bin
    lib_dir[1]=$path_jhome/jre/bin/j9vm

    IFS=":"
    set -A lib_path_arr1 $v_lib_path
    typeset -i libvals=0
    typeset -i match_count=0

    for var in ${lib_dir[*]} ; do
    while [[ $libvals -lt ${#lib_path_arr1[*]} ]] ;do
    if [[ $var = ${lib_path_arr1[$libvals]} ]] ;then
    match_count=1
    break
    else
    match_count=0
    fi
    libvals=$libvals+1
    done
    if [[ $match_count = 1 ]] ;then
    echo " JAVA ENV          : $var exists in the LIBPATH environment variable "
    else
    echo " $var does not exist in LIBPATH environment variable "
    fi
    libvals=0
    done
    echo "                      "
    echo " LIBRARY VERIFICATION "
    echo " ==================== "
    typeset -i eqpos4=11
    typeset -i str_len4=${#v_classpath}
    v_class_path=`expr substr "$v_classpath" "${eqpos4}" "${str_len4}"`
    set -A class_path_arr1
    class_path_arr1[0]=SS_4TRESS_PUBLIC_Client.jar
    class_path_arr1[1]=authentication-t24server.jar
    class_path_arr1[2]=jbossall-client.jar
    class_path_arr1[3]=temenos-commons-logging.jar
    class_path_arr1[4]=commons-codec-1.3.jar
    class_path_arr1[5]=authentication-webcommon.jar
    class_path_arr1[6]=authapi.jar
    class_path_arr1[7]=log4j-1.2.13.jar
    class_path_arr1[8]=log4j.properties

    typeset -i incr=0
    typeset -i ct1=0
    while [[ $incr -lt ${#class_path_arr1[*]} ]] ; do
    ct1=`echo $v_class_path | grep -c ${class_path_arr1[$incr]}`
    if [[ $ct1 =  0 ]] ;then
    echo "${class_path_arr1[$incr]} entry is missing in CLASSPATH environment.vars"
    fi
    incr=$incr+1
    ct1=0
    done


# Checking  existence class path
    IFS=":"
    set -A class_path_arr2 $v_class_path
    typeset -i cp1=0
    while [[ $cp1 -lt ${#class_path_arr2[*]} ]] ; do
    if [[ ${class_path_arr2[$cp1]} = "." ]] ; then
    echo " CLASSPATH  to  current Directory exist "
    elif [[ -f  ${class_path_arr2[$cp1]} ]] ; then
    echo " ${class_path_arr2[$cp1]} is present "
    else
    echo " ${class_path_arr2[$cp1]} is not present  "
    fi
    cp1=$cp1+1
    done
    unset IFS
    echo "               "
    echo " SERVER.CONFIG "
    echo " ============= "
    typeset -i ser_cgf=1
    typeset -i iter=0
    typeset -i v_iter=0
    typeset -i match_ct=0
    set -A eqpos5

# extracting server.config location from  JBCJVMOPT1
    if [[ jbc_count -gt 0 ]] ; then

    while [[ $v_iter -lt ${#v_jbcjvmopt[*]} ]] ; do
    match_ct=`echo ${v_jbcjvmopt[$v_iter]} | grep -c "ARC_CONFIG_PATH"`
    if [[ $match_ct -gt 0 ]] ; then
    v_jbcjvmopt1=${v_jbcjvmopt[$v_iter]}
    break
    fi
    v_iter=$v_iter+1
    match_ct=0
    done

    while [[ $ser_cgf -le ${#v_jbcjvmopt1} ]] ; do
    xy=`expr substr "$v_jbcjvmopt1" "$ser_cgf" 1`
    if [[ $xy = "=" ]] ; then
    eqpos5[$iter]=$ser_cgf
    iter=$iter+1
    fi
    ser_cgf=$ser_cgf+1
    done
    typeset -i begin=${eqpos5[0]}+3
    typeset -i end=${eqpos5[1]}-$begin
    v_chk=`expr substr "$v_jbcjvmopt1" "${begin}" "${end}"`

    server_config=
    typeset -i begin1=${eqpos5[1]}+1
    if [[ $v_chk = "ARC_CONFIG_PATH" ]] ; then
    server_config=`expr substr "$v_jbcjvmopt1" "${begin1}" "${#v_jbcjvmopt1}"`
    fi
    echo " SERVER CONFIG      : $server_config"
    else
      echo " JBCJVMOPTION is not present  "
    fi

# getting the Security Policy jar files location
    echo "                  "
    echo " POLICY JAR FILES "
    echo " ================ "

    jar_file_loc1=$path_jhome/jre/lib/security/US_export_policy.jar
    jar_file_loc2=$path_jhome/jre/lib/security/local_policy.jar

    typeset -i std_size1=3715
    typeset -i std_size2=3726

    typeset -i size1=0
    typeset -i size2=0
    size1=`ls -ltr $jar_file_loc1 | awk '{print $5'}`
    size2=`ls -ltr $jar_file_loc2 | awk '{print $5'}`

    if [[ $size1 = $std_size1 && $size2 = $std_size2 ]] ; then
    echo " INFO : $jar_file_loc1  : $std_size1   "
    echo " INFO : $jar_file_loc2      : $std_size2   "
    else
    echo " ERROR : Copy appropriate jar files to the $path_jhome/jre/lib/security  "
    fi
# Checking the keystore value in java.security
    typeset -i jceks_lnno=0
    security_file=$path_jhome/jre/lib/security/java.security
    jceks_lnno=`grep -c  'keystore.type=JCEKS' $security_file`
    if [[ $jceks_lnno > 0 ]]  ;then
    echo " Key Store type is verified and value is set to JCEKS "
    else
    echo " ERROR :  Key Store type is verified and value is not set to JCEKS "
    fi

# Checking server.config location and extracting the encrypted user and pass and location of test.jceks file

    filename=$server_config

# Decalre an int variable, an array.Using While loop read the file and store it in array arr1
    if [[ $jbc_count = 0 ]] ; then
       echo "JBCJVMOPTIONS is needed to execute the remaining script  "
       exit 0
    fi
    typeset -i count=0
    set -A arr1

    while read LINE
    do
    arr1[$count]=$LINE
    count=$count+1
    done < $filename

# Getting the Line No. Decalre j,u,p,k variable initialize it to 0


    typeset -i j=0
    typeset -i u=0
    typeset -i p=0
    typeset -i k=0

# iterate through each lin of the array and get the lin no by know length of the string

    while [[ $j -lt ${#arr1[*]} ]]
    do
    x=${arr1[$j]}
    y=`expr substr "$x" 1 39`
    z=`expr substr "$x" 1 43`
    a=`expr substr "$x" 1 36`
    if [[ $y == "temenos.arc.security.ftress.upauth.user" ]] ;then
    u=$j
    fi
    if [[ $z == "temenos.arc.security.ftress.upauth.password" ]] ;then
    p=$j
    fi
    if [[ $a == "temenos.arc.security.crypto.keystore" ]] ;then
    k=$j
    fi
    j=$j+1
    done

    usern=${arr1[$u]}
    password=${arr1[$p]}
    keystore=${arr1[$k]}

    echo "            "
    echo " ENCRYPTION "
    echo " ========== "
# Checking for existence of test.jceks
    typeset -i t=1
    typeset -i quote_pos=0
    jceks_path=

    while [[ $t -le ${#keystore} ]]
    do
    zks=`expr substr "$keystore" "$t" 1`
    if [[ $zks = '"' ]] ;then
    quote_pos=$t
    break
    else
    quote_pos=37
    fi
    t=$t+1
    done
    if [[ $quote_pos = 38 ]] ;then

    typeset -i pos=${#keystore}
    typeset -i begin=$quote_pos+1
    typeset -i end=$pos-$begin
    jceks_path=`expr substr "$keystore" "${begin}" "${end}"`
    else
    typeset -i pos=${#keystore}
    typeset -i begin=$quote_pos+1
    typeset -i end=$pos-$begin
    end=$end+1
    jceks_path=`expr substr "${keystore}" ${begin} ${end}`
    fi

    if [[ -f $jceks_path ]] ;then
    echo " INFO :JCE key store  file exists in $jceks_path "
    else
    echo " ERROR : JCE key store  file does not exists in $jceks_path "
    fi

# extracting the encrypted user text
# iterate thro string character by character get the position of '"' if exists or not exists

    typeset -i g=1
    typeset -i startpos=0
    enc_user=

    while [[ $g -le ${#usern} ]]
    do
    z=`expr substr "$usern" "$g" 1`
    if [[ $z = '"' ]] ;then
    startpos=$g
    break
    else
    startpos=40
    fi
    g=$g+1
    done

# if condition to decrypt the extracted username .

    if [[ $startpos = 41 ]] ;then
    typeset -i pos=${#usern}
    typeset -i begin=$startpos+1
    typeset -i end=$pos-$begin
    enc_user=`expr substr "$usern" ${begin} ${end}`
    else
    typeset -i pos=${#usern}
    typeset -i begin=$startpos+1
    typeset -i end=$pos-$begin
    end=$end+1
    enc_user=`expr substr "$usern" ${begin} ${end}`
    #echo " Encrypted User Name : $enc_user "
    fi
    typeset -i gre_count1=0
    `java -DARC_CONFIG_PATH=${HOME}/server.config -DARC_CONFIG_APP_NAME=ARC -jar ${HOME}/tools/encryption_tool/crypto.jar D $enc_user Y 2>/dev/null | grep -c "RESULT:" >result.txt`
     gre_count1=`grep -c "1" result.txt`
     if [[ $gre_count1 -gt 0 ]] ; then
       echo " INFO : Decryption is successfull for USER "
     else
       echo " ERROR : Decryption is unsuccessfull for USER "
     fi
#extracting the encrypted password text

    typeset -i f=1
    typeset -i startpos_pass=0
    enc_pass=

    while [[ $f -le ${#usern} ]]
    do
    zpass=`expr substr "$password" "$f" 1`
    if [[ $zpass = '"' ]] ;then
    startpos_pass=$f
    break
    else
    startpos_pass=44
    fi
    f=$f+1
    done
# if condition to decrypt the extracted username .
    if [[ $startpos_pass = 45 ]] ;then
    typeset -i pos=${#password}
    typeset -i begin=$startpos_pass+1
    typeset -i end=$pos-$begin
    enc_pass=`expr substr "$password" ${begin} ${end}`
    else
    typeset -i pos=${#password}
    typeset -i begin=$startpos_pass+1
    typeset -i end=$pos-$begin
    end=$end+1
    #echo $pos
    #echo $begin
    #echo $end

    enc_pass=`expr substr "$password" ${begin} ${end}`
    #echo "Encrypted Password : $enc_pass "
    fi
    typeset -i gre_count2=0
    `java -DARC_CONFIG_PATH=${HOME}/server.config -DARC_CONFIG_APP_NAME=ARC -jar ${HOME}/tools/encryption_tool/crypto.jar D $enc_pass Y 2>/dev/null | grep -c "RESULT:" >result1.txt`
    gre_count2=`grep -c "1" result1.txt`
    if [[ $gre_count2 -gt 0 ]] ;then
       echo " INFO : Decryption is successfull for Password  "
    else
       echo " ERROR :Decryption is unsuccessfull for Password "
    fi

    echo "                        "
    echo " T24 SERVER ENVIRONMENT "
    echo " ====================== "
    echo "                        "
    echo " is 4TRESS Server deployed on JBOSS or WEBSPHERE?  "
    echo " For JBOSS PRESS  1 or  FOR WEBSPHERE PRESS  2    "

    typeset -i Ftress_inst=0
    check(){
    read Ftress_inst
    }
    check

    until [[ $Ftress_inst = 1 || $Ftress_inst = 2 ]] ; do
    echo " Please re-enter either 1 or 2"
    check
    done

    set -A jboss_arr
    set -A websphere_arr
    set -A jbcjvmopt_val1

# common specific values to lookup in jbcjvmopt
    jbcjvmopt_val1[0]="com.temenos.t24.commons.logging.LoggerFactory=com.temenos.t24.commons.logging.impl.Log4jLoggerFactory"
    #jbcjvmopt_val1[1]="ARC_CONFIG_APP_NAME=ARC"

# initialize jboss array with specific value

    jboss_arr[0]="java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory"
    jboss_arr[1]="java.naming.provider.url=jnp:"
    jboss_arr[2]="java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces"

#initialize websphere array with specific value
    websphere_arr[0]="java.naming.factory.initial=com.ibm.websphere.naming.WsnInitialContextFactory"
    websphere_arr[1]="java.naming.provider.url=iiop:"
    websphere_arr[2]="java.naming.factory.url.pkgs=com.ibm.websphere.naming"

    typeset -i incre=0
    typeset -i counting1=0
    for var12 in ${jbcjvmopt_val1[*]} ; do
    while [[ $incre -lt ${#v_jbcjvmopt[*]} ]] ; do
    counting1=`echo ${v_jbcjvmopt[$incre]} | grep -c $var12`
    if [[ $counting1 -gt 0 ]] ;then
    echo " INFO : $var12 entry found in JBCJVMOPTION "
    break
    fi
    incre=$incre+1
    done
    if [[ $counting1 = 0 ]] ; then
    echo "WARNING : $var12 entry missing in JBCJVMOPTION "
    fi
    incre=0
    done
    typeset -i incr_ct=0
    typeset -i counting=0
    if [[ $Ftress_inst = 1 ]] ;then
       for var12 in ${jboss_arr[*]} ; do
          while [[ $incr_ct -lt ${#v_jbcjvmopt[*]} ]] ; do
               counting=`echo ${v_jbcjvmopt[$incr_ct]} | grep -c $var12`
               if [[ $counting -gt 0 ]] ;then
                  echo "$var12 entry found in JBCJVMOPTION "
                  break
               fi
               incr_ct=$incr_ct+1
          done
        if [[ $counting = 0 ]] ; then
            echo "ERROR : $var12 missing  in JBCJVMOPTION "
        fi
        incr_ct=0
      done
    elif [[ $Ftress_inst = 2 ]] ;then
        for var12 in ${websphere_arr[*]} ; do
           while [[ $incr_ct -lt ${#v_jbcjvmopt[*]} ]] ; do
             counting=`echo ${v_jbcjvmopt[$incr_ct]} | grep -c $var12`
             if [[ $counting -gt 0 ]] ;then
               echo " INFO : $var12 entry found in JBCJVMOPTION "
               break
             fi
             incr_ct=$incr_ct+1
            done
            if [[ $counting = 0 ]] ; then
               echo "ERROR : $var12 missing  in JBCJVMOPTION "
            fi
            incr_ct=0
           done
    else
      echo "Un printable statement "
    fi
    exit 0
