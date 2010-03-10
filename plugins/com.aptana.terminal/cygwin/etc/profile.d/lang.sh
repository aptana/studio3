# if no locale variable is set, indicate terminal charset via LANG
test -z "${LC_ALL:-${LC_CTYPE:-$LANG}}" && export LANG=C.UTF-8
