#don't use this - anki.js has been changed since, so it needs to be reconverted.

# -*- mode: coffee -*-

isIpad = Ti.Platform.model == "iPad"

VERSION = "AnkiMobile-1.8"
BASE_URL = "http://ankiweb.net/"
SYNC_URL = BASE_URL + "sync/"
#var BASE_URL = "http://192.168.1.14:8001/";
UPDATE_URL = BASE_URL + "update/"

Ti.include "mustache.js"

appDir = Titanium.Filesystem.applicationSupportDirectory
DBFILE = Titanium.Filesystem.getFile(appDir, "database")
MEDIAFILE = Titanium.Filesystem.getFile(appDir, "media")
DOCUMENTSFILE = Titanium.Filesystem.getFile(appDir)
MEDIAFILE.createDirectory true unless MEDIAFILE.exists()
DBPATH = DBFILE.getNativePath()
MEDIAPATH = MEDIAFILE.getNativePath()

# seed
Math.random new Date().getSeconds()

# Utils
######################################################################

assert = (x, y) ->
  throw (message: y and y or "Assertion failed") unless x

repr = (obj) ->
  a = []
  if typeof obj == "string"
    "'" + obj + "'"
  else if typeof obj == "number"
    obj.toString()
  else
    for x of obj
      a.push "{0}: {1}".format(x, (if typeof obj[x] == "string" then "'{0}'".format(obj[x]) else obj[x]))  if obj.hasOwnProperty(x) and typeof obj[x] != "function"
    "{{0}}".format a.join()

dump = (x) ->
  debug repr(x)
debug = Ti.API.debug
info = Ti.API.info

checkRel = (card) ->
  if card.successive
    msg = "r card but rd = " + card.relativeDelay  unless card.relativeDelay == 1
  else if card.reps
    msg = "f card but rd = " + card.relativeDelay  unless card.relativeDelay == 0
  else
    msg = "n card but rd = " + card.relativeDelay  unless card.relativeDelay == 2
  alert2 "Warning", msg + ". Please Tools>Advanced>Check DB on desktop"  if msg

alert2 = (title, msg, buttons, cb) ->
  buttons = [ "OK" ]  unless buttons
  unless msg
    msg = title
    title = "Error"
  diag = Titanium.UI.createAlertDialog(
    title: title
    message: msg
    buttonNames: buttons
  )
  diag.addEventListener "click", cb  if cb
  diag.show()

String::format = ->
  pattern = /\{\d+\}/g
  args = arguments
  @replace pattern, (capture) ->
    args[capture.match(/\d+/)]

String::capitalize = ->
  @replace /(^|\s)([a-z])/g, (m, p1, p2) ->
    p1 + p2.toUpperCase()

# Anki Utils
######################################################################

anki = {}
anki.utils = {}
anki.utils.genID = ->
  while true
    id = "{0}{1}".format(Math.floor(Math.random() * 1000000000000000), Math.floor(Math.random() * 1000))
    continue  if id.charAt(0) == "9"
    id = "-" + id  if Math.random() < 0.5
    break
  id

anki.utils.filtermap = (func, seq) ->
  newseq = []
  i = 0

  while i < seq.length
    res = func(seq[i])
    newseq.push res  if res != null
    i++
  newseq

anki.utils.removeHoles = (seq) ->
  anki.utils.filtermap ((e) ->
    e
  ), seq

anki.utils.reduce = (func, seq) ->
  res = 0
  i = 0

  while i < seq.length
    res = func(res, seq[i])
    i++
  res

anki.utils.dict = (seq) ->
  d = {}
  i = 0

  while i < seq.length
    d[seq[i][0]] = seq[i][1]
    i++
  d

anki.utils.ids2str = (ids) ->
  "(" + ids.join(",") + ")"

anki.utils.rand = (min, max) ->
  Math.random() * (max - min) + min

anki.utils.strip = (s) ->
  s.replace /^ +| +$/g, ""

anki.utils.set = (l) ->
  result = {}
  i = 0

  while i < l.length
    result[l[i]] = true
    i++
  result

anki.utils.deset = (s) ->
  result = []
  for e of s
    result.push e  if s.hasOwnProperty(e)
  result

anki.utils.keys = (h) ->
  res = []
  for e of h
    res.push e  if h.hasOwnProperty(e)
  res

anki.utils.values = (h) ->
  res = []
  for e of h
    res.push h[e]  if h.hasOwnProperty(e)
  res

anki.utils.dumb = (s) ->
  return s.substring(1)  if s
  s

dumb = anki.utils.dumb
anki.utils.dumbCol = (seq, col) ->
  col = 0  if typeof col == "undefined"
  i = 0

  while i <= seq.length
    seq[i][col] = anki.utils.dumb(seq[i][col])
    i++
  seq

anki.utils.timeTable =
  years: "{0} years"
  months: "{0} months"
  days: "{0} days"
  hours: "{0} hours"
  minutes: "{0} minutes"
  seconds: "{0} seconds"

anki.utils.shortTimeTable =
  years: "{0}y"
  months: "{0}m"
  days: "{0}d"
  hours: "{0}h"
  minutes: "{0}m"
  seconds: "{0}s"

anki.utils.time = ->
  new Date().getTime() / 1000

anki.utils.optimalPeriod = (time, point) ->
  abs = Math.abs

  if abs(time) < 60
    type = "seconds"
    point -= 1
  else if abs(time) < 3599
    type = "minutes"
  else if abs(time) < 60 * 60 * 24
    type = "hours"
  else if abs(time) < 60 * 60 * 24 * 30
    type = "days"
  else if abs(time) < 60 * 60 * 24 * 365
    type = "months"
    point += 1
  else
    type = "years"
    point += 1
  [ type, Math.max(point, 0) ]

anki.utils.convertSecondsTo = (seconds, type) ->
  if type == "seconds"
    seconds
  else if type == "minutes"
    seconds / 60.0
  else if type == "hours"
    seconds / 3600.0
  else if type == "days"
    seconds / 86400.0
  else if type == "months"
    seconds / 2592000.0
  else
    seconds / 31536000.0

anki.utils.fmtTimeSpan = (time, pad, point, isShort) ->
  pad = 0  unless pad
  point = 0  unless point
  ret = anki.utils.optimalPeriod(time, point)
  type = ret[0]
  point = ret[1]
  time = anki.utils.convertSecondsTo(time, type)

  if isShort
    fmt = anki.utils.shortTimeTable[type]
  else
    fmt = anki.utils.timeTable[type]
  fmt.format time.toFixed(point)

anki.utils.fmtFloat = (flt) ->
  ret = flt.toFixed(1)
  if isNaN(ret)
    0
  else
    ret

anki.utils.pad = (number, length) ->
  str = "" + number
  while str.length < length
    str = "0" + str
  str

anki.utils.getJSON = (url, args, onload, onerr) ->
  currentXhr = Titanium.Network.createHTTPClient()
  currentXhr.setTimeout 60000
  currentXhr.onerror = (e) ->
    if onerr
      onerr()
      return
    currentXhrFile.deleteFile()
    alert2 "Connection Error", "Please try again."
    if anki and anki.app
      anki.app.activityOff()
      anki.app.syncer.deck.close()  if anki.app.syncer and anki.app.syncer.deck

  currentXhr.onload = ->
    try
      txt = currentXhrFile.read().text
      currentXhrFile.deleteFile()
      onload JSON.parse(txt)
    catch err
      if onerr
        onerr()
        return
      s = url.split("/")
      alert "Deck: {0}\nAction: {1}\n{2}".format(anki.deckManager.currentDeck, s[s.length - 1], err.message or err.toString())
      if anki and anki.app
        anki.app.activityOff()
        anki.app.syncer.deck.close()  if anki.app.syncer and anki.app.syncer.deck

  currentXhr.open "POST", url
  currentXhrFile = Ti.Filesystem.createTempFile()
  currentXhr.file = currentXhrFile
  currentXhr.send args

anki.utils._days_in_month = [ 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ]
anki.utils._days_before_month = [ 0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 ]
anki.utils.is_leap = (year) ->
  year % 4 == 0 and (year % 100 != 0 or year % 400 == 0)

anki.utils.days_in_month = (year, month) ->
  if month == 2 and anki.utils.is_leap(year)
    29
  else
    anki.utils._days_in_month[month]

anki.utils.days_before_month = (year, month) ->
  days = anki.utils._days_before_month[month]
  ++days  if month > 2 and anki.utils.is_leap(year)
  days

anki.utils.days_before_year = (year) ->
  y = year - 1
  if y >= 0
    y * 365 + Math.floor(y / 4) - Math.floor(y / 100) + Math.floor(y / 400)
  else
    -366

anki.utils.fromordinal = (ordinal) ->
  DI4Y = 1461
  DI100Y = 36524
  DI400Y = 146097
  --ordinal
  n400 = Math.floor(ordinal / DI400Y)
  n = ordinal % DI400Y
  year = n400 * 400 + 1
  n100 = Math.floor(n / DI100Y)
  n = n % DI100Y
  n4 = Math.floor(n / DI4Y)
  n = n % DI4Y
  n1 = Math.floor(n / 365)
  n = n % 365
  year += n100 * 100 + n4 * 4 + n1
  if n1 == 4 or n100 == 4
    year -= 1
    month = 12
    day = 31
    return [ year, month, day ]
  leapyear = n1 == 3 and (n4 != 24 or n100 == 3)
  month = (n + 50) >> 5
  preceding = (anki.utils._days_before_month[month] + (month > 2 and leapyear))
  if preceding > n
    month -= 1
    preceding -= anki.utils.days_in_month(year, month)
  n -= preceding
  day = n + 1
  [ year, month, day ]

anki.utils.toordinal = (year, month, day) ->
  anki.utils.days_before_year(year) + anki.utils.days_before_month(year, month) + day

anki.utils.strToOrdinal = (str) ->
  m = /(\d{4})-(\d{2})-(\d{2})/.exec(str)
  anki.utils.toordinal parseInt(m[1], 10), parseInt(m[2], 10), parseInt(m[3], 10)

anki.utils.ordinalToStr = (ord) ->
  fmt = "{0}-{1}-{2}"
  arr = anki.utils.fromordinal(ord)
  fmt.format arr[0], anki.utils.pad(arr[1], 2), anki.utils.pad(arr[2], 2)

anki.utils.jsDateToStr = (date) ->
  "{0}-{1}-{2}".format date.getUTCFullYear(), anki.utils.pad(date.getUTCMonth() + 1, 2), anki.utils.pad(date.getUTCDate(), 2)

anki.utils.copyData = (src, dst) ->
  for e of src
    dst[e] = src[e]  if src.hasOwnProperty(e)

anki.utils.openDB = (path) ->
  Ti.Database.install "nt.anki.mp3", "mydb2"

anki.utils.DB = (db) ->
  db.execute.apply = Function::apply  if typeof db.execute.apply != "function"
  executeFull: ->
    args = Array::slice.call(arguments)
    dump args
    db.execute.apply db, arguments

  execute: db.execute
  close: db.close
  lastRowId: ->
    db.lastInsertRowId

  rowsAffected: ->
    db.rowsAffected

  _copyRow: (row) ->
    x = []
    cnt = row.fieldCount()
    i = 0
    while i < cnt
      x.push row.field(i)
      i++
    x

  scalar: ->
    r = @execute.apply(null, arguments)
    if r
      ret = (if r.isValidRow() then r.field(0) else null)
      r.close()
      ret
    else
      null

  first: ->
    r = @execute.apply(null, arguments)
    if r and r.isValidRow()
      ret = @_copyRow(r)
      r.close()
      ret
    else
      debug "not valid " + arguments[0] + " " + arguments[1] + " " + arguments[2]
      null

  all: ->
    x = []
    r = @execute.apply(null, arguments)
    if r
      while r.isValidRow()
        x.push @_copyRow(r)
        r.next()
      r.close()
      x
    else
      x

  column0: ->
    x = []
    r = @execute.apply(null, arguments)
    if r
      while r.isValidRow()
        x.push r.field(0)
        r.next()
      r.close()
      x
    else
      x

  column0d: ->
    x = []
    r = @execute.apply(null, arguments)
    if r
      while r.isValidRow()
        x.push dumb(r.field(0))
        r.next()
      r.close()
    x

anki.openDeck = (path, name, create, fast) ->
  assert path, "no deck path"
  deck =
    PRIORITY_HIGH: 4
    PRIORITY_MED: 3
    PRIORITY_NORM: 2
    PRIORITY_LOW: 1
    PRIORITY_NONE: 0
    PRIORITY_REVEARLY: -1
    PRIORITY_BURIED: -2
    PRIORITY_SUSPENDED: -3
    MATURE_THRESHOLD: 21
    NEW_CARDS_DISTRIBUTE: 0
    NEW_CARDS_LAST: 1
    NEW_CARDS_FIRST: 2
    NEW_CARDS_RANDOM: 0
    NEW_CARDS_OLD_FIRST: 1
    NEW_CARDS_NEW_FIRST: 2
    REV_CARDS_OLD_FIRST: 0
    REV_CARDS_NEW_FIRST: 1
    REV_CARDS_DUE_FIRST: 2
    REV_CARDS_RANDOM: 3
    DECK_VERSION: 61
    factorFour: 1.3
    initialFactor: 2.5
    minimumAverage: 1.7
    maxScheduleTime: 36500
    newCardOrderLabels: [ "Show in Random Order", "Show Oldest First", "Show Newest First" ]
    newCardSchedulingLabels: [ "Mix New and Old", "New Cards Last", "New Cards First" ]
    revCardOrderLabels: [ "Largest Interval First", "Smallest Interval First", "In Order Due", "In Random Order" ]
    failedCardOptionLabels: [ "Failed Cards Soon", "Failed Cards Last", "Failed Cards in 10 min", "Failed Cards in 8 hrs", "Failed Cards in 3 days" ]
    db: anki.utils.DB(Ti.Database.open(path))
    path: path
    name: name
    init: ->
      if create
        @id = 1
        throw "nyi"
      else
        @fromDB()
        @db.execute "pragma cache_size = 2000"
      @mediaDir = anki.deckManager._deckMediaDirFile(@name).getNativePath()
      if fast
        @resetFetched()
        @_globalStats = @createStats(@STATS_LIFE)
        @_dailyStats = @createStats(@STATS_DAY)
        return
      unless @db.scalar("select 1 from deckVars where key='revSpacing'")
        @setVarDefault "perDay", true
        @setVarDefault "newActive", ""
        @setVarDefault "revActive", ""
        @setVarDefault "revInactive", @suspended
        @setVarDefault "newInactive", @suspended
        @setVarDefault "newSpacing", 60
        @setVarDefault "mediaURL", ""
        @setVarDefault "revSpacing", 0.1
      @upgrade()
      @newGotten = 0
      @updateCutoff()
      @queueLimit = 100
      @setupStandardScheduler()
      @updateDynamicIndices()
      @averageFactor = @getInt("factor") or 2.5
      @delay1 = 600  if @delay1 > 7
      str = ("select 'i'||id from cards where type > 2 or priority " + "between -2 and -1")
      ids = @db.column0d(str)
      if ids.length
        @updatePriorities ids
        @db.execute "update cards set type = relativeDelay where type > 2"
      @reset()

    upgrade: ->
      return  if @version == @DECK_VERSION
      assert 0, "too old"  if @version < 39
      if @version < 62
        anki.app.activityOn "Upgrading {0}...".format(@name)
        anki.app.addSyncMessage "(this can take a long time)"
        inds = [ "intervalDesc", "intervalAsc", "randomOrder", "dueAsc", "dueDesc" ]
        i = 0

        while i < inds.length
          @db.execute "drop index if exists ix_cards_{0}2".format(inds[i])
          i++
        @db.execute "drop index if exists ix_cards_typeCombined"
        @db.execute "drop index if exists ix_cards_factId"
        @rebuildTypes()
        if @getBool("perDay")
          @hardIntervalMin = Math.max(1.0, @hardIntervalMin)
          @hardIntervalMax = Math.max(1.1, @hardIntervalMax)
        anki.app.addSyncMessage "(step 2/4)"
        @db.execute "update fieldModels set editFontFamily = 1"
        @db.execute "update cards set type = type - 3 where type between 0 and 2 " + "and priority = -3"
        anki.app.addSyncMessage "(step 3/4)"
        @addIndices()
        anki.app.addSyncMessage "(step 4/4)"
        @version = 62
        @toDB()

    addIndices: ->
      indices = [ [ "ix_cards_typeCombined", "cards (type, combinedDue, factId)" ], [ "ix_cards_relativeDelay", "cards (relativeDelay)" ], [ "ix_cards_modified", "cards (modified)" ], [ "ix_facts_modified", "facts (modified)" ], [ "ix_cards_priority", "cards (priority)" ], [ "ix_cards_factId", "cards (factId)" ] ]

      i = 0

      while i < indices.length
        name = indices[i][0]
        schema = indices[i][1]
        unless @db.scalar("select 1 from sqlite_master where name = ?", name)
          @db.execute "create index {0} on {1}".format(name, schema)
          idxAdded = true
        i++
      @db.execute "analyze"  if idxAdded

    setVarDefault: (key, value) ->
      @db.execute "insert or ignore into deckVars values (?, ?)", key, value

    fromDB: ->
      r = @db.first("select id,'i'||created,'i'||modified," + "description,version,'i'||currentModelId,syncName,'i'||lastSync," + "hardIntervalMin,hardIntervalMax,midIntervalMin,midIntervalMax," + "easyIntervalMin,easyIntervalMax,delay0,delay1,delay2," + "collapseTime,highPriority,medPriority,lowPriority,suspended," + "newCardOrder,newCardSpacing,failedCardMax,newCardsPerDay," + "sessionRepLimit,sessionTimeLimit,utcOffset,cardCount," + "factCount,failedNowCount,failedSoonCount,revCount,newCount," + "revCardOrder from decks")
      @id = parseInt(r[0], 10)
      @created = parseFloat(dumb(r[1]))
      @modified = parseFloat(dumb(r[2]))
      @description = r[3]
      @version = parseInt(r[4], 10)
      @currentModelId = dumb(r[5])
      @syncName = r[6]
      @lastSync = parseFloat(dumb(r[7]))
      @hardIntervalMin = parseFloat(r[8])
      @hardIntervalMax = parseFloat(r[9])
      @midIntervalMin = parseFloat(r[10])
      @midIntervalMax = parseFloat(r[11])
      @easyIntervalMin = parseFloat(r[12])
      @easyIntervalMax = parseFloat(r[13])
      @delay0 = parseInt(r[14], 10)
      @delay1 = parseInt(r[15], 10)
      @delay2 = parseFloat(r[16])
      @collapseTime = parseInt(r[17], 10)
      @highPriority = r[18]
      @medPriority = r[19]
      @lowPriority = r[20]
      @suspended = r[21]
      @newCardOrder = parseInt(r[22], 10)
      @newCardSpacing = parseInt(r[23], 10)
      @failedCardMax = parseInt(r[24], 10)
      @newCardsPerDay = parseInt(r[25], 10)
      @sessionRepLimit = parseInt(r[26], 10)
      @sessionTimeLimit = parseInt(r[27], 10)
      @utcOffset = parseInt(r[28], 10)
      @cardCount = parseInt(r[29], 10)
      @factCount = parseInt(r[30], 10)
      @failedNowCount = parseInt(r[31], 10)
      @failedSoonCount = parseInt(r[32], 10)
      @revCount = parseInt(r[33], 10)
      @newCount = parseInt(r[34], 10)
      @revCardOrder = parseInt(r[35], 10)

    toDB: ->
      @db.execute "update decks set created=?,modified=?,description=?,version=?," + "currentModelId=?,syncName=?,lastSync=?,hardIntervalMin=?," + "hardIntervalMax=?,midIntervalMin=?,midIntervalMax=?," + "easyIntervalMin=?,easyIntervalMax=?,delay0=?,delay1=?," + "delay2=?,collapseTime=?,highPriority=?,medPriority=?," + "lowPriority=?,suspended=?,newCardOrder=?,newCardSpacing=?," + "failedCardMax=?,newCardsPerDay=?,sessionRepLimit=?," + "sessionTimeLimit=?,utcOffset=?,cardCount=?,factCount=?," + "failedNowCount=?,failedSoonCount=?,revCount=?,newCount=?," + "revCardOrder=?", @created, @modified, @description, @version, @currentModelId, @syncName, @lastSync, @hardIntervalMin, @hardIntervalMax, @midIntervalMin, @midIntervalMax, @easyIntervalMin, @easyIntervalMax, @delay0, @delay1, @delay2, @collapseTime, @highPriority, @medPriority, @lowPriority, @suspended, @newCardOrder, @newCardSpacing, @failedCardMax, @newCardsPerDay, @sessionRepLimit, @sessionTimeLimit, @utcOffset, @cardCount, @factCount, @failedNowCount, @failedSoonCount, @revCount, @newCount, @revCardOrder

    setModified: ->
      @modified = anki.utils.time()
      @db.execute "update decks set modified = ?", @modified

    flushMod: ->
      @setModified()
      @toDB()

    close: ->
      @db.close()

    sessionLimitReached: ->
      return true  if @sessionTimeLimit and anki.utils.time() > (@sessionStartTime + @sessionTimeLimit)
      return true  if @sessionRepLimit and @sessionRepLimit <= @_dailyStats.reps - @sessionStartReps
      false

    updateDynamicIndices: ->
      indices =
        intervalDesc: "(type, priority desc, interval desc, factId, combinedDue)"
        intervalAsc: "(type, priority desc, interval, factId, combinedDue)"
        randomOrder: "(type, priority desc, factId, ordinal, combinedDue)"
        dueAsc: "(type, priority desc, due, factId, combinedDue)"
        dueDesc: "(type, priority desc, due desc, factId, combinedDue)"

      required = []
      required.push "intervalDesc"  if @revCardOrder == @REV_CARDS_OLD_FIRST
      required.push "intervalAsc"  if @revCardOrder == @REV_CARDS_NEW_FIRST
      required.push "randomOrder"  if @revCardOrder == @REV_CARDS_RANDOM
      required.push "dueAsc"  if @revCardOrder == @REV_CARDS_DUE_FIRST or @newCardOrder == @NEW_CARDS_OLD_FIRST or @newCardOrder == @NEW_CARDS_RANDOM
      required.push "dueDesc"  if @newCardOrder == @NEW_CARDS_NEW_FIRST
      changed = false
      for k of indices
        if indices.hasOwnProperty(k)
          have = @db.scalar("select 1 from sqlite_master where name = 'ix_cards_{0}2'".format(k))
          unless required.indexOf(k) == -1
            unless have
              changed = true
              @db.execute "create index ix_cards_{0}2 on cards {1}".format(k, indices[k])
          else
            if have
              changed = true
              @db.execute "drop index ix_cards_" + k + "2"
      @db.execute "analyze"  if changed

    lowerCase: (str) ->
      @db.scalar "select lower(?)", str

    saveFactPris: (fid) ->
      @db.all "select 'i'||id, priority, type from cards where factId = ? " + "and type between 0 and 2", fid

    restoreFactPris: (fid, pris) ->
      i = 0

      while i < pris.length
        id = anki.utils.dumb(pris[i][0])
        @db.execute "update cards set priority = ?, type = ? " + "where id = ?", pris[i][1], pris[i][2], id
        i++

    wrapQA: (type, cmid, txt) ->
      alignment = @cardModelAlignment(cmid, type)

      if alignment == 0
        align = "center"
      else if alignment == 1
        align = "left"
      else
        align = "right"
      div = "<div align={4} class=\"card{0}\" id=\"cm{1}{2}\">{3}</div>".format(type[0], type[0], @hexFromCache(cmid), txt, align)
      div

    createCard: (id) ->
      card =
        deck: this
        tags: ""
        type: 2
        relativeDelay: 2
        isDue: true
        timerStarted: false
        timerStopped: false
        modified: anki.utils.time()
        init: ->
          if id
            card.fromDB id
          else
            assert false, "can't create card"
          card

        hasTag: (tag) ->
          tags = @deck.db.scalar("select lower(tags) from facts where factId = ?", @factId)
          tags = @deck.parseTags(tags)
          tag = @deck.lowerCase(tag)
          i = 0

          while i < tags.length
            return true  if tags[i] == tag
            i++
          false

        setModified: ->
          @modified = anki.utils.time()

        startTimer: ->
          @timerStarted = anki.utils.time()

        stopTimer: ->
          @timerStopped = anki.utils.time()

        thinkingTime: ->
          (@timerStopped or anki.utils.time()) - @timerStarted

        totalTime: ->
          anki.utils.time() - @timerStarted

        genFuzz: ->
          @fuzz = 1 + (Math.random() / 10 - 0.05)

        htmlQuestion: (type) ->
          type = "question"  if typeof type == "undefined"
          @deck.wrapQA type, @cardModelId, this[type]

        htmlAnswer: ->
          @htmlQuestion "answer"

        fromDB: (id) ->
          r = @deck.db.first("select 'i'||factId,'i'||cardModelId,'i'||created,'i'||modified,tags,ordinal," + "question,answer,priority,interval,lastInterval,'i'||due," + "'i'||lastDue,factor,lastFactor,'i'||firstAnswered,reps,successive" + ",averageTime,reviewTime,youngEase0,youngEase1," + "youngEase2,youngEase3,youngEase4,matureEase0," + "matureEase1,matureEase2,matureEase3,matureEase4," + "yesCount,noCount,'i'||spaceUntil,isDue,type,'i'||combinedDue," + "relativeDelay from cards where id = ?", id)
          throw (message: "can't fetch card, id was {0} {1}".format(id, typeof id))  unless r
          @id = id
          @factId = dumb(r[0])
          @cardModelId = dumb(r[1])
          @created = parseFloat(dumb(r[2]))
          @modified = parseFloat(dumb(r[3]))
          @tags = r[4]
          @ordinal = parseInt(r[5], 10)
          @question = r[6]
          @answer = r[7]
          @priority = parseInt(r[8], 10)
          @interval = parseFloat(r[9])
          @lastInterval = parseFloat(r[10])
          @due = parseFloat(dumb(r[11]))
          @lastDue = parseFloat(dumb(r[12]))
          @factor = parseFloat(r[13])
          @lastFactor = parseFloat(r[14])
          @firstAnswered = parseFloat(dumb(r[15]))
          @reps = parseInt(r[16], 10)
          @successive = parseInt(r[17], 10)
          @averageTime = parseFloat(r[18])
          @reviewTime = parseFloat(r[19])
          @youngEase0 = parseInt(r[20], 10)
          @youngEase1 = parseInt(r[21], 10)
          @youngEase2 = parseInt(r[22], 10)
          @youngEase3 = parseInt(r[23], 10)
          @youngEase4 = parseInt(r[24], 10)
          @matureEase0 = parseInt(r[25], 10)
          @matureEase1 = parseInt(r[26], 10)
          @matureEase2 = parseInt(r[27], 10)
          @matureEase3 = parseInt(r[28], 10)
          @matureEase4 = parseInt(r[29], 10)
          @yesCount = parseInt(r[30], 10)
          @noCount = parseInt(r[31], 10)
          @spaceUntil = parseFloat(dumb(r[32]))
          @isDue = parseInt(r[33], 10)
          @type = parseInt(r[34], 10)
          @combinedDue = parseFloat(dumb(r[35]))
          @relativeDelay = parseInt(r[36], 10)

        toDB: ->
          checkRel this
          @deck.db.execute "update cards set modified=?,tags=?,interval=?,lastInterval=?," + "due=?,lastDue=?,factor=?,lastFactor=?,firstAnswered=?," + "reps=?,successive=?,averageTime=?,reviewTime=?," + "youngEase0=?,youngEase1=?,youngEase2=?,youngEase3=?," + "youngEase4=?,matureEase0=?,matureEase1=?,matureEase2=?," + "matureEase3=?,matureEase4=?,yesCount=?,noCount=?," + "spaceUntil=?,isDue=?,type=?,combinedDue=max(?,?)," + "priority=?,relativeDelay=? where id=?", @modified, @tags, @interval, @lastInterval, @due, @lastDue, @factor, @lastFactor, @firstAnswered, @reps, @successive, @averageTime, @reviewTime, @youngEase0, @youngEase1, @youngEase2, @youngEase3, @youngEase4, @matureEase0, @matureEase1, @matureEase2, @matureEase3, @matureEase4, @yesCount, @noCount, @spaceUntil, @isDue, @type, @due, @spaceUntil, @priority, @relativeDelay, @id

        updateStats: (ease, state) ->
          @reps += 1
          if ease > 1
            @successive += 1
          else
            @successive = 0
          delay = Math.min(@totalTime(), 60)
          @reviewTime += delay
          if @averageTime
            @averageTime = (@averageTime + delay) / 2.0
          else
            @averageTime = delay
          state = "young"  if state == "new"
          attr = state + "Ease" + ease
          this[attr] += 1
          if ease < 2
            @noCount += 1
          else
            @yesCount += 1
          @firstAnswered = anki.utils.time()  unless @firstAnswered
          @setModified()

      card.init()

    deleteCards: (ids) ->
      return  unless ids

      now = anki.utils.time()
      strids = anki.utils.ids2str(ids)
      factIds = @db.column0d("select 'i'||factId from cards where id in " + strids)
      @db.execute "delete from cards where id in " + strids
      i = 0
      while i < ids.length
        @db.execute "insert into cardsDeleted values (?, ?)", ids[i], now
        i++
      tags = @db.column0d("select 'i'||tagId from cardTags where cardId in " + strids)
      @db.execute "delete from cardTags where cardId in " + strids
      unused = []
      i = 0
      while i < tags.length
        unused.push tags[i]  unless @db.scalar("select 1 from cardTags where tagId = ? limit 1", tags[i])
        i++
      @db.execute "delete from tags where id in {0} and priority = 2".format(anki.utils.ids2str(unused))
      @deleteDanglingFacts()
      @flushMod()

    deleteFact: (id) ->
      @db.execute "insert into cardsDeleted select id, ? from cards " + "where factId = ?", anki.utils.time(), id
      @db.execute "delete from cards where factId = ?", id
      @deleteFacts [ id ]
      @flushMod()

    deleteFacts: (ids) ->
      return  unless ids
      now = anki.utils.time()
      strids = anki.utils.ids2str(ids)
      @db.execute "delete from facts where id in " + strids
      @db.execute "delete from fields where factId in " + strids
      i = 0

      while i < ids.length
        @db.execute "insert into factsDeleted values (?, ?)", ids[i], now
        i++
      @setModified()

    deleteDanglingFacts: ->
      ids = @db.column0d("select 'i'||facts.id from facts where facts.id not in " + "(select distinct factId from cards)")
      @deleteFacts ids
      ids

    setupStandardScheduler: ->
      @getCardId = @_getCardId
      @fillFailedQueue = @_fillFailedQueue
      @fillRevQueue = @_fillRevQueue
      @fillNewQueue = @_fillNewQueue
      @rebuildFailedCount = @_rebuildFailedCount
      @rebuildRevCount = @_rebuildRevCount
      @rebuildNewCount = @_rebuildNewCount
      @requeueCard = @_requeueCard
      @timeForNewCard = @_timeForNewCard
      @updateNewCountToday = @_updateNewCountToday
      @cardType = @_cardType
      @finishScheduler = null
      @answerCard = @_answerCard
      @cardLimit = @_cardLimit
      @scheduler = "standard"
      @answerPreSave = null
      @cardQueue = @_cardQueue
      @spaceCards = @_spaceCards
      @resetAfterReviewEarly()

    getCounts: ->
      @counts[0]

    saveCounts: ->
      @counts.push [ @failedSoonCount, @revCount, @newCountToday ]

    _cardQueue: (card) ->
      @cardType card

    fillQueues: ->
      @fillFailedQueue()
      @fillRevQueue()
      @fillNewQueue()

    rebuildCounts: ->
      t = anki.utils.time()
      @cardCount = @db.scalar("select count(*) from cards")
      @factCount = @db.scalar("select count(*) from facts")
      @rebuildFailedCount()
      @rebuildRevCount()
      @rebuildNewCount()
      @counts = []

    _cardLimit: (active, inactive, sql) ->
      _yes = @parseTags(@getVar(active))
      _no = @parseTags(@getVar(inactive))
      repl = null

      if _yes.length
        yids = anki.utils.values(@tagIds(_yes))
        nids = anki.utils.values(@tagIds(_no))
        sql.replace "where", ("where +c.id in (select cardId from cardTags where " + "tagId in {0}) and +c.id not in (select cardId from " + "cardTags where tagId in {1}) and").format(anki.utils.ids2str(yids), anki.utils.ids2str(nids))
      else if _no.length
        nids = anki.utils.values(@tagIds(_no))
        sql.replace "where", "where +c.id not in (select cardId from cardTags where " + "tagId in {0}) and".format(anki.utils.ids2str(nids))
      else
        sql

    _rebuildFailedCount: ->
      @failedSoonCount = @db.scalar(@cardLimit("revActive", "revInactive", "select count(c.id) from cards c where type = 0 and " + "combinedDue < ?"), @failedCutoff)

    _rebuildRevCount: ->
      @revCount = @db.scalar(@cardLimit("revActive", "revInactive", "select count(c.id) from cards c where type = 1 and " + "combinedDue < ?"), @dueCutoff)

    _rebuildNewCount: ->
      @newCount = @db.scalar(@cardLimit("newActive", "newInactive", "select count(c.id) from cards c where type = 2 and " + "combinedDue < ?"), @dueCutoff)
      @updateNewCountToday()
      @spacedCards = []

    _updateNewCountToday: ->
      @newCountToday = Math.max(Math.min(@newCount, @newCardsPerDay - @newCardsDoneToday() - @newGotten), 0)

    _fillFailedQueue: ->
      if @failedSoonCount and not @failedQueue.length
        @failedQueue = @db.all(@cardLimit("revActive", "revInactive", ("select 'i'||c.id, 'i'||factId, combinedDue from cards c where " + "type = 0 and combinedDue < ? order by combinedDue " + "limit {0}").format(@queueLimit)), @failedCutoff)
        @failedQueue.reverse()

    _fillRevQueue: ->
      if @revCount and not @revQueue.length
        @revQueue = @db.all(@cardLimit("revActive", "revInactive", ("select 'i'||c.id, 'i'||factId from cards c where " + "type = 1 and combinedDue < ? order by {0} " + "limit {1}").format(@revOrder(), @queueLimit)), @dueCutoff)
        @revQueue.reverse()

    _fillNewQueue: ->
      if @newCount and not @newQueue.length and not @spacedCards.length
        @newQueue = @db.all(@cardLimit("newActive", "newInactive", ("select 'i'||c.id, 'i'||factId from cards c where " + "type = 2 and combinedDue < ? order by {0} " + "limit {1}").format(@newOrder(), @queueLimit)), @dueCutoff)
        @newQueue.reverse()

    queueNotEmpty: (queue, fillFunc, new_) ->
      while true
        @removeSpaced queue, new_
        return true  if queue.length
        fillFunc()
        return false  unless queue.length

    removeSpaced: (queue, new_) ->
      popped = []
      delay = null
      while queue.length
        fid = dumb(queue[queue.length - 1][1])
        if fid of @spacedFacts
          id = dumb(queue.pop()[0])
          if new_ and @newSpacing < @queueLimit * 6
            popped.push id
            delay = @spacedFacts[fid]
        else
          @spacedCards.push [ delay, popped ]  if popped.length
          return

    revNoSpaced: ->
      @queueNotEmpty @revQueue, @fillRevQueue

    newNoSpaced: ->
      @queueNotEmpty @newQueue, @fillNewQueue, true

    _requeueCard: (card, oldSuc) ->
      checkRel card
      if card.type == 2
        id = @newQueue.pop()[0]
      else if card.type == 0
        id = @failedQueue.pop()[0]
      else
        id = @revQueue.pop()[0]
      assert 0, "requeue wrong"  unless dumb(id) == card.id

    revOrder: ->
      [ "priority desc, interval desc", "priority desc, interval", "priority desc, combinedDue", "priority desc, factId, ordinal" ][@revCardOrder]

    newOrder: ->
      [ "priority desc, due", "priority desc, due", "priority desc, due desc" ][@newCardOrder]

    rebuildTypes: ->
      @db.execute "update cards set type = (case when successive then 1 when reps " + "then 0 else 2 end), relativeDelay = (case when successive " + "then 1 when reps then 0 else 2 end) where type >= 0"
      @db.execute "update cards set type = type - 3 where priority = -3 " + "and type >= 0"

    _cardType: (card) ->
      if card.successive
        1
      else if card.reps
        0
      else
        2

    updateCutoff: ->
      date = new Date(new Date().getTime() - (@utcOffset * 1000))
      date = new Date(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate() + 1)
      offset = @utcOffset - (date.getTimezoneOffset() * 60)
      cutoff = (date.getTime() / 1000.0) + offset
      while cutoff <= anki.utils.time()
        cutoff += 86400
      cutoff = Math.min(anki.utils.time() + 86400, cutoff)
      @failedCutoff = cutoff
      if @getBool("perDay")
        @dueCutoff = @failedCutoff
      else
        @dueCutoff = anki.utils.time()

    reset: ->
      @resetFetched()
      @_globalStats = @createStats(@STATS_LIFE)
      @_dailyStats = @createStats(@STATS_DAY)
      @rebuildCounts()
      @failedQueue = []
      @revQueue = []
      @newQueue = []
      @spacedFacts = {}
      @newFromCache = {}
      @newGotten = 0
      if @newCardSpacing == @NEW_CARDS_DISTRIBUTE
        if @newCountToday
          @newCardModulus = ((@newCountToday + @revCount) / @newCountToday)
          @newCardModulus = Math.max(2, @newCardModulus)  if @revCount
          @newCardModulus = Math.round(@newCardModulus)
        else
          @newCardModulus = 0
      else
        @newCardModulus = 0
      @rebuildCSS()
      @newSpacing = @getFloat("newSpacing")
      @revSpacing = @getFloat("revSpacing")

    checkDay: ->
      if @_dailyStats.today() != @_dailyStats.day
        @updateCutoff()
        @reset()
        return true
      false

    resetFetched: ->
      ids = @db.column0d("select 'i'||id from cards where type between 9 and 11")
      @db.execute "update cards set type = type - 9 where type between 9 and 11"  if ids

    setupReviewEarlyScheduler: ->
      @fillRevQueue = @_fillRevEarlyQueue
      @rebuildRevCount = @_rebuildRevEarlyCount
      @finishScheduler = @_onReviewEarlyFinished
      @answerPreSave = @_reviewEarlyPreSave
      @scheduler = "reviewEarly"

    _reviewEarlyPreSave: (card, ease) ->
      card.type += 6  if ease > 1

    resetAfterReviewEarly: ->
      ids = @db.column0d("select 'i'||id from cards where type between 6 and 8 union " + "select 'i'||id from cards where priority = -1")
      if ids
        @updatePriorities ids
        @db.execute "update cards set type = type - 6 where type between 6 and 8"

    _onReviewEarlyFinished: ->
      @resetAfterReviewEarly()
      @setupStandardScheduler()

    _rebuildRevEarlyCount: ->
      @revCount = @db.scalar(@cardLimit("revActive", "revInactive", "select count() from cards c where type = 1 and " + "combinedDue > ?"), @dueCutoff)

    _fillRevEarlyQueue: ->
      if @revCount and not @revQueue.length
        @revQueue = @db.all(@cardLimit("revActive", "revInactive", "select 'i'||id, 'i'||factId from cards c where type = 1 " + "and combinedDue > ? order by combinedDue " + "limit {0}".format(@queueLimit)), @dueCutoff)
        @revQueue.reverse()

    setupLearnMoreScheduler: ->
      @rebuildNewCount = @_rebuildLearnMoreCount
      @updateNewCountToday = @_updateLearnMoreCountToday
      @finishScheduler = @setupStandardScheduler
      @scheduler = "learnMore"

    _rebuildLearnMoreCount: ->
      @newCount = @db.scalar(@cardLimit("newActive", "newInactive", "select count(c.id) from cards c where type = 2 and " + "combinedDue < ?"), @dueCutoff)

    _updateLearnMoreCountToday: ->
      @newCountToday = @newCount

    setupCramScheduler: (active, order) ->
      @getCardId = @_getCramCardId
      @activeCramTags = active
      @cramOrder = order
      @rebuildNewCount = @_rebuildCramNewCount
      @rebuildRevCount = @_rebuildCramCount
      @rebuildFailedCount = @_rebuildFailedCramCount
      @fillRevQueue = @_fillCramQueue
      @fillFailedQueue = @_fillFailedCramQueue
      @finishScheduler = @setupStandardScheduler
      @failedCramQueue = []
      @requeueCard = @_requeueCramCard
      @answerCard = @_answerCramCard
      @cardLimit = @_cramCardLimit
      @answerPreSave = @_reviewEarlyPreSave
      @cardQueue = @_cramCardQueue
      @scheduler = "cram"
      @spaceCards = @_spaceCramCards
      @answerPreSave = @_cramPreSave
      @cramTypes = {}
      @finishScheduler = @_onReviewEarlyFinished

    _spaceCramCards: (card) ->
      @spacedFacts[card.factId] = anki.utils.time() + @newSpacing

    _cramCardQueue: (card) ->
      @cramTypes[card.id]

    _cramPreSave: (card, ease) ->
      card.lastInterval = @cramLastInterval
      card.type += 6

    _answerCramCard: (card, ease) ->
      @cramLastInterval = card.lastInterval
      @_answerCard card, ease
      @failedCramQueue.unshift [ "i" + card.id, "i" + card.factId ]  if ease == 1

    _getCramCardId: (check) ->
      @fillQueues()
      check = true  if typeof (check) == "undefined"
      dumbLast = (l) ->
        dumb l[l.length - 1][0]

      if @failedCardMax and @failedSoonCount >= @failedCardMax
        id = dumbLast(@failedQueue)
        @cramTypes[id] = 0
        return id
      if @revNoSpaced()
        id = dumbLast(@revQueue)
        @cramTypes[id] = 1
        return id
      if @failedQueue.length
        id = dumbLast(@failedQueue)
        @cramTypes[id] = 0
        return id
      if check
        @spacedFacts = {}
        return @getCardId(false)
      null

    _requeueCramCard: (card, oldSuc) ->
      if @cardQueue(card) == 1
        assert @revQueue.length, "requeuecram1"
        @revQueue.pop()
      else
        assert @failedCramQueue.length, "requeuecram2"
        @failedCramQueue.pop()

    _rebuildCramNewCount: ->
      @newCount = 0
      @newCountToday = 0

    _cramCardLimit: (active, sql) ->
      _yes = @parseTags(active)
      if _yes.length
        yids = anki.utils.values(@tagIds(_yes))
        sql.replace "where", "where +c.id in (select cardId from cardTags where " + "tagId in {0}) and".format(anki.utils.ids2str(yids))
      else
        sql

    _fillCramQueue: ->
      if @revCount and not @revQueue.length
        @revQueue = @db.all(@cardLimit(@activeCramTags, ("select 'i'||id, 'i'||factId from cards c where type " + "between 0 and 2 order by {0} limit {1}").format(@cramOrder, @queueLimit)))
        @revQueue.reverse()

    _rebuildCramCount: ->
      @revCount = @db.scalar(@cardLimit(@activeCramTags, "select count(*) from cards c where type between 0 and 2"))

    _rebuildFailedCramCount: ->
      @failedSoonCount = @failedCramQueue.length

    _fillFailedCramQueue: ->
      @failedQueue = @failedCramQueue

    getCard: ->
      id = @getCardId(true)
      if id
        card = @createCard(id)
        @spacedFacts[card.factId] = anki.utils.time() + @newSpacing  if @newSpacing
        card.queue = @cardQueue(card)
        @saveCounts()
        if card.queue == 0
          @failedSoonCount -= 1
        else if card.queue == 1
          @revCount -= 1
        else
          @newCount -= 1
          @newGotten += 1
        @requeueCard card, card.successive  unless (id of @newFromCache)
        @db.execute "update cards set type = type + 9 where id = ?", card.id
        card.genFuzz()
        card
      else
        null

    _getCardId: (check) ->
      @fillQueues()
      @updateNewCountToday()
      dumbLast = (l) ->
        dumb l[l.length - 1][0]

      if @failedQueue.length
        return dumbLast(@failedQueue)  if @failedQueue[@failedQueue.length - 1][2] + @delay0 < anki.utils.time()  if @delay0
        return dumbLast(@failedQueue)  if @failedCardMax and @failedSoonCount >= (@failedCardMax - 1)
      return @getNewCard()  if @newNoSpaced() and @timeForNewCard()
      return dumbLast(@revQueue)  if @revNoSpaced()
      if @newCountToday
        id = @getNewCard()
        return id  if id
      return dumbLast(@failedQueue)  if @showFailedLast() and @failedQueue.length
      null

    _timeForNewCard: ->
      return false  unless @newCountToday
      return false  if @newCardSpacing == @NEW_CARDS_LAST
      return true  if @newCardSpacing == @NEW_CARDS_FIRST
      return false  if @db.scalar("select 1 from cards where id = ? and priority = 4", dumb(@revQueue[@revQueue.length - 1][0]))  if @revQueue.length
      if @newCardModulus
        @_dailyStats.reps % @newCardModulus == 0
      else
        false

    getNewCard: ->
      src = null
      if @spacedCards.length and @spacedCards[0][0] < anki.utils.time()
        src = 0
      else if @newQueue.length
        src = 1
      else if @spacedCards.length
        src = 0
      else
        return null
      if src == 0
        ent = @spacedCards.shift()
        return null  unless ent
        id = ent[1].shift()
        @newFromCache[id] = ent[1]
        id
      else
        dumbLast = (l) ->
          dumb l[l.length - 1][0]

        dumbLast @newQueue

    showFailedLast: ->
      @collapseTime or not @delay0

    buryFact: (card) ->
      @db.execute "update cards set priority = -2, type = type + 3, " + "isDue=0 where factId = ?", card.factId

    saveUndoData: (card, type) ->
      daily = {}
      anki.utils.copyData @_dailyStats, daily
      global = {}
      anki.utils.copyData @_globalStats, global
      newCard = {}
      anki.utils.copyData card, newCard
      @db.execute "update decks set modified=?,failedSoonCount=?," + "revCount=?,newCount=?", @modified, @failedSoonCount, @revCount, @newCount
      card: newCard
      daily: daily
      global: global
      mod: @modified
      soonC: @failedSoonCount
      revC: @revCount
      newC: @newCount
      type: type

    undoAnswer: (data, revtoo) ->
      @db.execute "begin"
      data.card.toDB()
      @_dailyStats = data.daily
      @_dailyStats.toDB()
      @_globalStats = data.global
      @_globalStats.toDB()
      if revtoo
        time = @db.scalar("select 'i'||time from reviewHistory where cardId = ? " + "order by time desc limit 1", data.card.id)
        @db.execute "delete from reviewHistory where time = ?", dumb(time)
      @modified = data.mod
      @failedSoonCount = data.soonC
      @revCount = data.revC
      @newCount = data.newC
      @toDB()
      @db.execute "commit"

    _answerCard: (card, ease) ->
      now = anki.utils.time()
      oldState = @cardState(card)
      lastDelaySecs = anki.utils.time() - card.combinedDue
      lastDelay = lastDelaySecs / 86400.0
      oldSuc = card.successive
      oldQueue = card.queue
      last = card.interval
      card.interval = @nextInterval(card, ease)
      card.lastInterval = last
      if card.reps
        card.lastDue = card.due
      else
        @newGotten -= 1
      card.due = @nextDue(card, ease, oldState)
      card.isDue = 0
      card.lastFactor = card.factor
      card.spaceUntil = 0
      @updateFactor card, ease  unless @finishScheduler
      @spaceCards card

      if ease == 1
        if card.due < @failedCutoff
          @failedSoonCount += 1
          bumpFail = true
      card.updateStats ease, oldState
      card.type = @cardType(card)
      card.relativeDelay = card.type
      card.due = Math.max(card.due, @dueCutoff + 1)  unless ease == 1
      @answerPreSave card, ease  if @answerPreSave
      card.combinedDue = card.due
      card.toDB()
      @updateStats card, ease, oldState
      @updateHistory card, ease, lastDelay
      if @newFromCache[card.id]
        cards = @newFromCache[card.id]
        @spacedCards.push [ anki.utils.time() + @newSpacing, cards ]  if cards.length
        delete @newFromCache[card.id]
      isLeech = @isLeech(card)
      @handleLeech card  if isLeech
      @flushMod()
      @counts.shift()
      @counts[0][0] += 1  if @counts.length and bumpFail

    _spaceCards: (card) ->
      new_ = anki.utils.time() + @newSpacing
      @db.execute ("update cards set combinedDue = (case " + "when type = 1 then combinedDue + 86400 * (case " + " when interval*? < 1 then 0 else interval*? end)" + "when type = 2 then ? end)," + "modified = ?, isDue = 0 where id != ? and factId = ? " + "and combinedDue < ? and type between 1 and 2"), @revSpacing, @revSpacing, new_, anki.utils.time(), card.id, card.factId, @dueCutoff
      @spacedFacts[card.factId] = new_

    isLeech: (card) ->
      _no = card.noCount
      fmax = @getInt("leechFails")
      return false  unless fmax
      not card.successive and _no >= fmax and \
        (fmax - _no) % (Math.max(fmax / 2, 1)) == 0

    handleLeech: (card) ->
      @addTag card, "Leech"
      @suspendCards [ card.id ]  if @getInt("suspendLeeches")
      @leechCB()  if @leechCB

    nextInterval: (card, ease) ->
      delay = @_adjustedDelay(card, ease)
      @_nextInterval card, delay, ease

    _nextInterval: (card, delay, ease) ->
      interval = card.interval
      factor = card.factor
      if delay < 0 and card.successive
        interval = Math.max(card.lastInterval, card.interval + delay)
        interval = 0  if interval < @midIntervalMin
        delay = 0
      if ease == 1
        interval *= @delay2
        interval = 0  if interval < @hardIntervalMin
      else if interval == 0
        if ease == 2
          interval = anki.utils.rand(@hardIntervalMin, @hardIntervalMax)
        else if ease == 3
          interval = anki.utils.rand(@midIntervalMin, @midIntervalMax)
        else interval = anki.utils.rand(@easyIntervalMin, @easyIntervalMax)  if ease == 4
      else
        if interval < @hardIntervalMax and interval > 0.166
          mid = (@midIntervalMin + @midIntervalMax) / 2.0
          interval = mid / factor
        if ease == 2
          interval = (interval + delay / 4) * 1.2
        else if ease == 3
          interval = (interval + delay / 2) * factor
        else interval = (interval + delay) * factor * @factorFour  if ease == 4
        interval *= card.fuzz
      interval = Math.min(interval, @maxScheduleTime)  if @maxScheduleTime
      interval

    nextIntervalStr: (card, ease, isShort) ->
      inter = @nextInterval(card, ease)
      anki.utils.fmtTimeSpan inter * 86400, isShort

    nextDue: (card, ease, oldState) ->
      if ease == 1
        if oldState == "mature" and @delay1 and @delay1 != 600
          return @failedCutoff + (@delay1 - 1) * 86400
        else
          due = 0
      else
        due = card.interval * 86400.0
      due + anki.utils.time()

    updateFactor: (card, ease) ->
      card.lastFactor = card.factor
      card.factor = @averageFactor  unless card.reps
      if card.successive and not @cardIsBeingLearnt(card)
        if ease == 1
          card.factor -= 0.20
        else card.factor -= 0.15  if ease == 2
      card.factor += 0.10  if ease == 4
      card.factor = Math.max(1.3, card.factor)

    _adjustedDelay: (card, ease) ->
      return 0  if @cardIsNew(card)
      if card.combinedDue <= anki.utils.time()
        (anki.utils.time() - card.due) / 86400.0
      else
        (anki.utils.time() - card.combinedDue) / 86400.0

    randomizeNewCards: ->
      fids = @db.column0d("select distinct 'i'||factId from cards where relativeDelay = 2")
      t = anki.utils.time()
      i = 0

      while i < fids.length
        rand = Math.random() * t
        @db.execute "update cards set due = ? + ordinal, combinedDue = " + "max(? + ordinal, spaceUntil), modified = ? " + "where factId = ? and reps = 0", rand, rand, t, fids[i]
        i++

    orderNewCards: ->
      t = anki.utils.time()
      @db.execute "update cards set due = created, combinedDue = " + "max(spaceUntil, created), modified = ? where relativeDelay = 2", t

    nextDueMsg: ->
      next = @earliestTime()
      if next
        newCount = @newCardsDueBy(@dueCutoff + 86400)
        newCardsTomorrow = Math.min(newCount, @newCardsPerDay)
        cards = @cardsDueBy(@dueCutoff + 86400)
        msg = ("At this time tomorrow:<br>{0}<br>{1}").format("There will be <b class=bb>{0} reviews</b>.".format(cards), "There will be <b class=bb>{0} new</b> cards.".format(newCardsTomorrow))
        msg = "The next review is in <b>{0}</b>.".format(@earliestTimeStr())  if next - anki.utils.time() > 86400 and not newCardsTomorrow
      else
        msg = "No cards are due."
      msg

    earliestTime: ->
      earliestRev = @db.scalar(@cardLimit("revActive", "revInactive", "select combinedDue from cards c where type = 1 " + "order by combinedDue limit 1"))
      earliestFail = @db.scalar(@cardLimit("revActive", "revInactive", ("select combinedDue+{0} from cards c where type " + "= 0 order by combinedDue limit 1").format(@delay0)))
      if earliestRev and earliestFail
        Math.min earliestRev, earliestFail
      else if earliestRev
        earliestRev
      else
        earliestFail

    earliestTimeStr: (next) ->
      next = @earliestTime()  unless next
      return "unknown"  unless next
      diff = next - anki.utils.time()
      anki.utils.fmtTimeSpan diff

    cardsDueBy: (time) ->
      @db.scalar @cardLimit("revActive", "revInactive", "select count(c.id) from cards c where type between 0 and 1 and " + "combinedDue < ?"), time

    newCardsDueBy: (time) ->
      @db.scalar @cardLimit("newActive", "newInactive", "select count(c.id) from cards c where type = 2 and " + "combinedDue < ?"), time

    deckFinishedMsg: ->
      spaceSusp = ""

      c = @spacedCardCount()
      spaceSusp += "There are <b class=bb>{0} delayed</b> cards.".format(c)  if c
      c = @hiddenCards()
      if c
        spaceSusp += "<br>"  if spaceSusp
        spaceSusp += "Some cards are inactive or suspended"
      spaceSusp = "<br><br>" + spaceSusp  if spaceSusp
      ("<div style=\"white-space: normal;\">" + "<h1>Congratulations!</h1>You have finished for now." + "<br><br>{0}\n{1}</div>").format @nextDueMsg(), spaceSusp

    parseTags: (tags) ->
      tags = tags.split(RegExp(" |, ?"))
      anki.utils.filtermap ((x) ->
        s = anki.utils.strip(x)
        if s
          s
        else
          null
      ), tags

    joinTags: (tags) ->
      tags.join " "

    canonifyTags: (tags) ->
      tags.sort()

    addTag: (card, tag, removeOrToggle) ->
      fid = card.factId
      tags = @db.scalar("select tags from facts where id = ?", fid)
      lowered = @lowerCase(tag)
      tags = @parseTags(tags)
      found = false
      i = 0

      while i < tags.length
        if @lowerCase(tags[i]) == lowered
          found = i
          break
        i++
      if removeOrToggle == "toggle"
        if found == false
          removeOrToggle = false
        else
          removeOrToggle = true
      if (found == false) and not removeOrToggle
        tags.push tag
      else tags.splice found, 1  if (found != false) and removeOrToggle
      @canonifyTags tags
      @db.execute "update facts set tags = ?, modified = ? where id = ?", @joinTags(tags), anki.utils.time(), fid
      @db.execute "update cards set modified = ? where factId = ?", anki.utils.time(), fid
      card.setModified()
      @updateFactTags [ fid ]
      @updatePriorities @db.column0d("select 'i'||id from cards where factId = ?", fid)
      @setModified()
      not removeOrToggle

    removeTag: (card, tag) ->
      @addTag card, tag, true

    tagIds: (tags, create) ->
      create = true  if typeof create == "undefined"
      tagsD = {}
      ids = {}

      if create
        i = 0
        while i < tags.length
          @db.execute "insert or ignore into tags (tag) values (?)", tags[i]
          i++
      tagsR = @db.all("select lower(tag), id from tags where tag in ({0})".format(anki.utils.filtermap((x) ->
        "'{0}'".format x.replace(/\'/g, "''")
      , tags).join()))
      i = 0
      while i < tagsR.length
        tagsD[tagsR[i][0]] = tagsR[i][1]
        i++
      tagsD

    updateAllPriorities: (partial, dirty) ->
      dirty = true  if typeof dirty == "undefined"
      newp = @updateTagPriorities()
      unless partial
        tmp = @db.all("select 'i'||id, priority as pri from tags")
        newp = []
        i = 0

        while i < tmp.length
          newp.push
            id: dumb(tmp[0])
            pri: tmp[1]
          i++
      cids = @db.column0d("select distinct 'i'||cardId from cardTags where tagId in " + anki.utils.ids2str(anki.utils.filtermap((x) ->
        x.id
      , newp)))
      @updatePriorities cids, dirty

    updateTagPriorities: ->
      pris = [ [ @lowPriority, 1 ], [ @medPriority, 3 ], [ @highPriority, 4 ] ]
      i = 0
      while i < pris.length
        @tagIds @parseTags(pris[i][0])
        i++
      tags = @db.all("select lower(tag), 'i'||id, priority from tags")
      up = {}

      i = 0
      while i < pris.length
        type = @lowerCase(pris[i][0])
        pri = pris[i][1]
        tmptags = @parseTags(type)
        j = 0
        while j < tmptags.length
          up[tmptags[j]] = pri
          j++
        i++
      newp = []

      i = 0
      while i < tags.length
        tag = tags[i][0]
        id = dumb(tags[i][1])
        pri = tags[i][2]
        if (tag of up) and up[tag] != pri
          newp.push
            id: id
            pri: up[tag]

          @db.execute "update tags set priority = ? where id = ?", up[tag], id
        else if not (tag of up) and pri != 2
          newp.push
            id: id
            pri: 2

          @db.execute "update tags set priority = 2 where id = ?", id
        i++
      newp

    updatePriorities: (cardIds, dirty) ->
      assert cardIds != null, "no cardIds"

      if cardIds.length > 1000
        limit = ""
      else
        limit = "and cardTags.cardId in " + anki.utils.ids2str(cardIds)
      cards = @db.all(("select 'i'||cardTags.cardId, case " + "when max(tags.priority) > 2 then max(tags.priority) " + "when min(tags.priority) = 1 then 1 " + "else 2 end from cardTags, tags " + "where cardTags.tagId = tags.id " + "{0} group by cardTags.cardId").format(limit))

      dumb0 = (x) ->
        if x[1] == pri
          dumb x[0]
        else
          null

      pri = 0
      while pri < 5
        cs = anki.utils.filtermap(dumb0, cards)
        if cs
          if dirty
            @db.execute "update cards set priority = ?, modified = ? " + "where id in " + anki.utils.ids2str(cs) + " and priority != ? and priority != -3", pri, anki.utils.time(), pri
          else
            @db.execute "update cards set priority = ? " + "where id in " + anki.utils.ids2str(cs) + " and priority != ? and priority != -3", pri, pri
        pri++

    allTags_: (where) ->
      where = ""  unless where
      t = @db.column0("select distinct tags from facts " + where)
      t = t.concat(@db.column0("select tags from models"))
      t = t.concat(@db.column0("select name from cardModels"))
      tags = anki.utils.deset(anki.utils.set(@parseTags(@joinTags(t))))
      tags.sort()
      tags

    updateFactTags: (factIds) ->
      @updateCardTags @db.column0d("select 'i'||id from cards where factId in " + anki.utils.ids2str(factIds))

    updateCardTags: (cardIds) ->
      if typeof cardIds == "undefined"
        @db.execute "delete from cardTags"
        @db.execute "delete from tags"
        tids = @tagIds(@allTags_())
        rows = @splitTagsList()
      else unless cardIds
        return
      else
        strcids = anki.utils.ids2str(cardIds)
        @db.execute "delete from cardTags where cardId in " + strcids
        strfids = anki.utils.ids2str(@db.column0d("select 'i'||factId from cards where " + "id in " + strcids))
        tids = @tagIds(@allTags_("where id in " + strfids))
        rows = @splitTagsList("and facts.id in " + strfids)
      d = []

      i = 0
      while i < rows.length
        id = dumb(rows[i][0])
        fact = rows[i][1]
        model = rows[i][2]
        templ = rows[i][3]
        tags = @parseTags(@lowerCase(fact))
        j = 0
        while j < tags.length
          @db.execute "insert into cardTags (cardId,tagId,src) " + "values (?,?,0)", id, tids[tags[j]]
          j++
        tags = @parseTags(@lowerCase(model))
        j = 0
        while j < tags.length
          @db.execute "insert into cardTags (cardId,tagId,src) " + "values (?,?,1)", id, tids[tags[j]]
          j++
        tags = @parseTags(@lowerCase(templ))
        j = 0
        while j < tags.length
          @db.execute "insert into cardTags (cardId,tagId,src) " + "values (?,?,2)", id, tids[tags[j]]
          j++
        i++
      @db.execute "delete from tags where priority = 2 and id not in " + "(select distinct tagId from cardTags)"

    splitTagsList: (where) ->
      where = ""  unless where
      @db.all "select 'i'||cards.id, facts.tags, models.tags," + "cardModels.name from cards, facts, models, " + "cardModels where cards.factId == facts.id and " + "facts.modelId == models.id and cards.cardModelId " + "= cardModels.id " + where

    toggleMark: (card) ->
      @addTag card, "Marked", "toggle"

    factTags: (fid) ->
      @db.scalar "select tags from facts where id = ?", fid

    factFields: (fid) ->
      res = @db.all("select 'i'||f.id, 'i'||fm.id, f.value, fm.name " + "from fields f, fieldModels fm where f.fieldModelId =" + " fm.id and factId = ? order by f.ordinal", fid)
      fields = []
      i = 0

      while i < res.length
        h =
          id: dumb(res[i][0])
          fmid: dumb(res[i][1])
          value: res[i][2].replace(/<br( \/)?>/g, "\n")
          name: res[i][3]

        fields.push h
        i++
      fields

    newFactFields: ->
      res = @db.all("select 'i'||fm.id, fm.name, fm.ordinal from fieldModels fm " + "where fm.modelId = ? order by fm.ordinal", @currentModelId)
      fields = []
      i = 0

      while i < res.length
        h =
          fmid: dumb(res[i][0])
          name: res[i][1]
          value: ""
          ordinal: res[i][2]

        fields.push h
        i++
      fields

    updateFact: (fid, fields, tags) ->
      i = 0

      while i < fields.length
        fields[i].value = fields[i].value.replace(/\n/g, "<br>")
        @db.execute "update fields set value = ? where id = ?", fields[i].value, fields[i].id
        i++
      @db.execute "update facts set modified = ?, tags = ? where id = ?", anki.utils.time(), tags, fid
      @updateQACache fid, fields, tags
      @setModified()
      @updateFactTags [ fid ]
      @updatePriorities @db.column0d("select 'i'||id from cards where factId = ?", fid)

    addFact: (fields, tags) ->
      @db.execute "begin"
      t = anki.utils.time()
      fid = anki.utils.genID()
      @db.execute "insert into facts (id, modelId, created, modified, " + "tags, spaceUntil, lastCardId) values (?, ?, ?, ?," + "?, 0, 0)", fid, @currentModelId, t, t, tags
      @updateFactTags [ fid ]

      i = 0

      while i < fields.length
        h = fields[i]
        @db.execute "insert into fields (id, factId, fieldModelId, " + "ordinal, value) values (?, ?, ?, ?, ?)", anki.utils.genID(), fid, h.fmid, h.ordinal, h.value
        i++
      @addCards fid
      @updateQACache fid, fields, tags
      @setModified()
      @db.execute "commit"

    previewFact: (fields, tags, cmids) ->
      res = []
      newFields = {}
      i = 0

      while i < fields.length
        newFields[fields[i].name] = [ fields[i].fmid, fields[i].value ]
        i++
      i = 0
      while i < cmids.length
        cinfo = @db.first("select 'i'||id, qformat, aformat from cardModels " + "where id = ?", cmids[i])
        cinfo[0] = dumb(cinfo[0])
        res.push @updateQAForCard_(cinfo, newFields, tags)
        i++
      res

    factValid: (fields, fid) ->
      i = 0

      while i < fields.length
        h = fields[i]
        res = @db.first("select required, fieldModels.\"unique\" from fieldModels " + "where id = ?", h.fmid)
        req = parseInt(res[0], 10)
        uniq = parseInt(res[1], 10)
        return h.name + " is required"  if req and not h.value.replace(/^ +| +$/, "")
        if uniq and h.value.replace(/^ +| +$/, "")
          txt = "select 1 from fields where fieldModelId = ? " + "and value = ?"

          if fid
            txt += " and factId != ?"
            c = @db.scalar(txt, h.fmid, h.value, fid)
          else
            c = @db.scalar(txt, h.fmid, h.value)
          return h.name + " is not unique"  if c
        i++
      null

    addCards: (fid, cmids) ->
      cmids = @db.all("select 'i'||cm.id, cm.ordinal from cardModels cm, facts f where " + "cm.modelId = f.modelId and f.id = ? and cm.active=1 " + "order by cm.ordinal", fid)  unless cmids
      t = @db.scalar("select created from facts where id = ?", fid)

      i = 0

      while i < cmids.length
        cmid = dumb(cmids[i][0])
        ord = cmids[i][1]
        t2 = t + 0.000001 * ord
        cid = anki.utils.genID()
        @db.execute "insert into cards (id, factId, cardModelId, created, " + "modified, tags, ordinal, question, answer, priority," + "interval, lastInterval, due, lastDue, factor," + "lastFactor, firstAnswered, reps, successive, averageTime," + "reviewTime, youngEase0, youngEase1, youngEase2, youngEase3," + "youngEase4, matureEase0, matureEase1, matureEase2," + "matureEase3, matureEase4, yesCount, noCount," + "spaceUntil, isDue, type, combinedDue," + "relativeDelay) values (" + "?, ?, ?, ?," + "?, ?, ?, ?, ?, ?," + "?, ?, ?, ?, ?," + "?, ?, ?, ?, ?," + "?, ?, ?, ?, ?," + "?, ?, ?, ?," + "?, ?, ?, ?, " + "?, ?, ?, ?, ?)", cid, fid, cmid, t2, t2, "", ord, "", "", 2, 0, 0, t2, 0, 2.5, 2.5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, t2, 2
        @updateCardTags [ cid ]
        @updatePriorities [ cid ]
        @cardCount += 1
        @newCount += 1
        i++

    updateQACache: (fid, fields, tags) ->
      cards = @db.all("select 'i'||c.id, cm.qformat, cm.aformat from cards c, cardModels cm " + "where factId = ? and c.cardModelId = cm.id", fid)
      newFields = {}
      cache = []
      i = 0
      while i < fields.length
        newFields[fields[i].name] = [ fields[i].fmid, fields[i].value ]
        cache.push fields[i].value
        i++
      @db.execute "update facts set spaceUntil = ? where id = ?", @stripHTMLMedia(cache.join(" ")), fid
      i = 0
      while i < cards.length
        cards[i][0] = dumb(cards[i][0])
        @updateQAForCard cards[i], newFields, tags
        i++

    updateQAForCard_: (cinfo, fields, tags) ->
      data = {}
      for e of fields
        if fields.hasOwnProperty(e)
          data["text:" + e] = @stripHTML(fields[e][1])
          if fields[e][1]
            data[e] = "<span class=\"fm{0}\">{1}</span>".format(@hexFromCache(fields[e][0]), fields[e][1])
          else
            data[e] = ""
      data.tags = tags
      data.Tags = tags
      data.modelTags = @db.scalar(("select name from models m, facts f, cards c where " + "m.id = f.id and f.id = c.factId and c.id = ?"), cinfo[0])
      data.cardModel = @db.scalar(("select name from cardModels cm, cards c where " + "cm.id=c.cardModelId and c.id =?"), cinfo[0])
      [ cinfo[0], @formatQA(cinfo[1], data), @formatQA(cinfo[2], data) ]

    updateQAForCard: (cinfo, fields, tags) ->
      res = @updateQAForCard_(cinfo, fields, tags)
      @db.execute "update cards set question = ?, answer = ?, " + "modified = ? where id = ?", res[1], res[2], anki.utils.time(), res[0]

    formatQA: (format, data) ->
      format = format.replace(/%\((.+?)\)s/g, "{{$1}}", format)
      Mustache.to_html format, data

    stripHTML: (text) ->
      text = text.replace(/<.*?>/g, "")
      text = text.replace(/&lt;/g, "<")
      text = text.replace(/&gt;/g, ">")
      text

    stripHTMLMedia: (s) ->
      s = s.replace(/<img src=[\"\']?([^\"\'>]+)[\"\']? ?\/?>/, " $1 ")
      @stripHTML s

    STATS_LIFE: 0
    STATS_DAY: 1
    createStats: (type, day, id) ->
      stats =
        deck: this
        id: null
        type: null
        day: day
        reps: 0
        averageTime: 0
        reviewTime: 0
        distractedTime: 0
        distractedReps: 0
        newEase0: 0
        newEase1: 0
        newEase2: 0
        newEase3: 0
        newEase4: 0
        youngEase0: 0
        youngEase1: 0
        youngEase2: 0
        youngEase3: 0
        youngEase4: 0
        matureEase0: 0
        matureEase1: 0
        matureEase2: 0
        matureEase3: 0
        matureEase4: 0
        toDB: ->
          @id = @deck.db.scalar("select id from stats where type = ? and day = ?", @type, @day)  unless @id
          unless @id
            @deck.db.execute "insert into stats (type,day,reps,averageTime," + "reviewTime,distractedTime,distractedReps,newEase0," + "newEase1,newEase2,newEase3,newEase4,youngEase0," + "youngEase1,youngEase2,youngEase3,youngEase4," + "matureEase0,matureEase1,matureEase2,matureEase3," + "matureEase4) values (?,?,0,0,0,0,0,0,0,0," + "0,0,0,0,0,0,0,0,0,0,0,0)", @type, @day
            @id = @deck.db.lastRowId()
          @deck.db.execute "update stats set type=?,day=?,reps=?,averageTime=?," + "reviewTime=?,distractedTime=?,distractedReps=?," + "newEase0=?,newEase1=?,newEase2=?,newEase3=?,newEase4=?," + "youngEase0=?,youngEase1=?,youngEase2=?,youngEase3=?," + "youngEase4=?,matureEase0=?,matureEase1=?,matureEase2=?," + "matureEase3=?,matureEase4=? where id = ?", @type, @day, @reps, @averageTime, @reviewTime, @distractedTime, @distractedReps, @newEase0, @newEase1, @newEase2, @newEase3, @newEase4, @youngEase0, @youngEase1, @youngEase2, @youngEase3, @youngEase4, @matureEase0, @matureEase1, @matureEase2, @matureEase3, @matureEase4, @id

        fromDB: ->
          r = @deck.db.first("select id,type,day,reps,averageTime,reviewTime,distractedTime," + "distractedReps,newEase0,newEase1,newEase2,newEase3," + "newEase4,youngEase0,youngEase1,youngEase2,youngEase3," + "youngEase4,matureEase0,matureEase1,matureEase2," + "matureEase3,matureEase4 from stats where id = ?", @id)
          @type = parseInt(r[1], 10)
          @day = r[2]
          @reps = parseInt(r[3], 10)
          @averageTime = parseFloat(r[4])
          @reviewTime = parseFloat(r[5])
          @distractedTime = parseFloat(r[6])
          @distractedReps = parseInt(r[7], 10)
          @newEase0 = parseInt(r[8], 10)
          @newEase1 = parseInt(r[9], 10)
          @newEase2 = parseInt(r[10], 10)
          @newEase3 = parseInt(r[11], 10)
          @newEase4 = parseInt(r[12], 10)
          @youngEase0 = parseInt(r[13], 10)
          @youngEase1 = parseInt(r[14], 10)
          @youngEase2 = parseInt(r[15], 10)
          @youngEase3 = parseInt(r[16], 10)
          @youngEase4 = parseInt(r[17], 10)
          @matureEase0 = parseInt(r[18], 10)
          @matureEase1 = parseInt(r[19], 10)
          @matureEase2 = parseInt(r[20], 10)
          @matureEase3 = parseInt(r[21], 10)
          @matureEase4 = parseInt(r[22], 10)

        today: ->
          date = new Date(new Date().getTime() - (@deck.utcOffset * 1000))
          anki.utils.jsDateToStr date

      if id
        stats.id = id
        stats.fromDB()
      else
        stats.type = type
        if type == @STATS_LIFE
          stats.id = 1
          stats.fromDB()
        else
          stats.day = stats.today()  unless stats.day
          stats.type = @STATS_DAY
          id = @db.scalar("select id from stats where type = ? and day = ?", stats.type, stats.day)
          if id
            stats.id = id
            stats.fromDB()
          else
            stats.toDB()
      stats

    createModel: (id) ->
      model =
        deck: this
        id: id
        init: ->
          if id
            @fromDB id
          else
            @id = anki.utils.genID()
            @deckId = 0
            @created = 0
            @modified = 0
            @tags = ""
            @name = ""
            @description = ""
            @features = ""
            @spacing = 0
            @initialSpacing = 0
            @source = 0
            @toDB()

        fromDB: (id) ->
          r = @deck.db.first("select deckId,'i'||created,'i'||modified,tags,name," + "description,features,spacing,initialSpacing,source " + "from models where id = ?", id)
          throw (message: "can't fetch model")  unless r
          @deckId = r[0]
          @created = parseFloat(dumb(r[1]))
          @modified = parseFloat(dumb(r[2]))
          @tags = r[3]
          @name = r[4]
          @description = r[5]
          @features = r[6]
          @spacing = parseFloat(r[7])
          @initialSpacing = parseInt(r[8], 10)
          @source = r[9]

        toDB: ->
          @deck.db.execute "insert into models values (?,1,0,0,\"\",\"\"," + "\"\",\"\",0,0,0)", @id  unless @deck.hasModel(@id)
          @deck.db.execute "update models set deckId=?,created=?,modified=?,tags=?," + "name=?,description=?,features=?,spacing=?," + "initialSpacing=?,source=? where id=?", @deckId, @created, @modified, @tags, @name, @description, @features, @spacing, @initialSpacing, @source, @id

      model.init()
      model

    addModel: (model) ->
      model.toDB()
      @currentModelId = model.id
      @flushMod()

    hasModel: (id) ->
      @db.scalar "select 1 from models where id = ?", id

    cardsForFact: (fid) ->
      @db.column0d "select 'i'||id from cards where factId = ?", fid

    suspendFact: (fid, rev) ->
      cids = @cardsForFact(fid)
      if rev
        @unsuspendCards cids
      else
        @suspendCards cids

    suspendCards: (ids) ->
      @db.execute "update cards set type = relativeDelay - 3," + "priority = -3, modified = ?, " + "isDue=0 where type >= 0 and id in " + anki.utils.ids2str(ids), anki.utils.time()
      @setModified()

    unsuspendCards: (ids) ->
      @db.execute "update cards set type = relativeDelay, priority=0, modified=? " + "where type < 0 and id in " + anki.utils.ids2str(ids), anki.utils.time()
      @updatePriorities ids
      @flushMod()

    hiddenCards: ->
      @db.scalar "select 1 from cards where combinedDue < ? " + "and type between 0 and 1 limit 1", @dueCutoff

    newCardsDoneToday: ->
      @_dailyStats.newEase0 + @_dailyStats.newEase1 + @_dailyStats.newEase2 + @_dailyStats.newEase3 + @_dailyStats.newEase4

    spacedCardCount: ->
      now = anki.utils.time()
      @db.scalar "select count(cards.id) from cards where " + "combinedDue > ? and due < ?", now, now

    matureCardCount: ->
      @db.scalar "select count(id) from cards where interval >= 21"

    youngCardCount: ->
      @db.scalar "select count(id) from cards where interval < 21 " + "and reps != 0"

    newCountAll: ->
      @db.scalar "select count(id) from cards where relativeDelay = 2"

    cardState: (card) ->
      if @cardIsNew(card)
        "new"
      else if card.interval > @MATURE_THRESHOLD
        "mature"
      else
        "young"

    cardIsNew: (card) ->
      card.reps == 0

    cardIsBeingLearnt: (card) ->
      card.lastInterval < 7

    cardIsYoung: (card) ->
      not @cardIsNew(card) and not @cardIsMature(card)

    cardIsMature: (card) ->
      card.interval >= @MATURE_THRESHOLD

    tagInfo: ->
      @db.all "select tag, count(cardId), tagId from tags, cardTags " + "where tags.id = cardTags.tagId group by tags.id " + "order by tag"

    getETA: (stats) ->
      count = stats.rev + stats["new"]
      count *= 1 + stats["gYoungNo%"] / 100.0
      left = count * stats.dAverageTime
      failedBaseMulti = 1.5
      failedMod = 0.07
      failedBaseCount = 20
      factor = (failedBaseMulti + (failedMod * (stats.failed - failedBaseCount)))
      left += stats.failed * stats.dAverageTime * factor
      left

    queueForCard: (card) ->
      if @cardIsNew(card)
        "new"
      else if card.successive == 0
        "failed"
      else
        "rev"

    updateStats: (card, ease, oldState) ->
      @updateStatsObj @_globalStats, card, ease, oldState
      @updateStatsObj @_dailyStats, card, ease, oldState

    updateStatsObj: (stats, card, ease, oldState) ->
      stats.reps += 1
      delay = card.totalTime()
      if delay >= 60
        stats.reviewTime += 60
      else
        stats.reviewTime += delay
        stats.averageTime = stats.reviewTime / stats.reps
      attr = oldState + "Ease" + ease
      stats[attr] += 1
      stats.toDB()

    updateHistory: (card, ease, delay) ->
      @db.execute "insert into reviewHistory values (" + "?,?,?,?,?,?,?,?,?,?,?,?)", card.id, anki.utils.time(), card.lastInterval, card.interval, ease, delay, card.lastFactor, card.factor, card.reps, card.thinkingTime(), card.yesCount, card.noCount

    rebuildCSS: ->
      @css = @getVar("cssCache")
      @hexCache = JSON.parse(@getVar("hexCache"))
      info = @db.all("select 'i'||cm.id, questionAlign, answerAlign, m.tags, " + "questionInAnswer, typeAnswer " + "from cardModels as cm, models as m where cm.modelId = m.id")
      @templateCache = {}
      i = 0

      while i < info.length
        @templateCache[dumb(info[i][0])] = ([ info[i][1], info[i][2], info[i][3], info[i][4], info[i][5] ])
        i++
      @furiganaType = @getVar("furiganaType") or "1"
      @_customCSS = @getVar("mobileCSS") or ""
      @_fontCSS = @getVar("fontCSS") or ""
      @_fontCSS = @_fontCSS.replace(/url\(\'/g, "url('" + @mediaDir + "/")
      @_js = @getVar("mobileJS") or ""
      @_mediaURL = @getVar("mediaURL")
      null

    addMediaUniquely: (mediaName, blob) ->
      m = mediaName.match(/(.+)\.(.+?)$/)
      root = m[1]
      ext = m[2]
      repl = (match, group) ->
        " ({0})".format 1 + Number(group)

      while true
        f = root + "." + ext
        unless anki.deckManager.haveMediaForDeck(@name, f)
          anki.deckManager.updateMediaFromFile @name, f, blob
          @addMediaEntry f, blob
          return f
        re = RegExp(" \\((\\d+)\\)$")
        root += " (1)"  unless root.match(re)
        root = root.replace(re, repl)

    addMediaEntry: (fname, blob) ->

    _rebuildCSS: ->
      debug "rebuildcss"
      assert 0, "nyi"
      genCSS = (prefix, row) ->
        tx = anki.utils.time()
        id = dumb(row[0])
        fam = row[1]
        siz = row[2]
        col = row[3]
        align = row[4]
        rtl = row[5]
        t = ""
        t += "font-family:\"{0}\";".format(fam)  if fam
        t += "font-size:{0}px;".format(siz)  if siz
        t += "color:{0};".format(col)  if col
        t += "direction:rtl;unicode-bidi:embed;"  if rtl == "rtl"
        if align != -1
          if align == 0
            align = "center"
          else if align == 1
            align = "left"
          else
            align = "right"
        t += "text-align:{0};".format(align)
        t = "{0}{1} {{2}}\n".format(prefix, anki.utils.hexifyID(id), t)  if t
        t

      css = anki.utils.filtermap((row) ->
        genCSS ".fm", row
      , @db.all("select 'i'||id, quizFontFamily, quizFontSize, quizFontColour," + "-1, features from fieldModels")).join()
      css += anki.utils.filtermap((row) ->
        genCSS "#cmq", row
      , @db.all("select 'i'||id, questionFontFamily, questionFontSize, " + "questionFontColour, questionAlign, 0 " + "from cardModels")).join()
      css += anki.utils.filtermap((row) ->
        genCSS "#cma", row
      , @db.all("select 'i'||id, answerFontFamily, answerFontSize, " + "answerFontColour, answerAlign, 0 " + "from cardModels")).join()
      css += anki.utils.filtermap((row) ->
        ".cmb{0} {background:{1};}\n".format anki.utils.hexifyID(dumb(row[0])), row[1]
      , @db.all("select 'i'||id, lastFontColour from cardModels")).join()
      @css = css
      css

    js: ->
      @_js

    customCSS: ->
      @_customCSS

    fontCSS: ->
      @_fontCSS

    fontFiles: ->
      re = /url\(\'(.+?)(#.+?)?\'\)/g

      res = []
      while (m = re.exec(@getVar("fontCSS") or ""))
        res.push m[1]
      res

    hexFromCache: (id) ->
      @hexCache[id]

    cardQuestionInAnswer: (id) ->
      @templateCache[id][3]

    cardTypeAnswerField: (id) ->
      @templateCache[id][4]

    cardTypeAnswer: (card) ->
      f = @cardTypeAnswerField(card.cardModelId)
      r = @db.scalar("select value from fields, fieldModels fm where " + "factId = ? and fieldModelId = fm.id and " + "fm.name = ?", card.factId, f)
      r

    cardModelAlignment: (id, type) ->
      if type == "question"
        @templateCache[id][0]
      else
        @templateCache[id][1]

    cardTagsFromCardModel: (id, type) ->
      @templateCache[id][2]

    getMobileScale: ->
      if isIpad
        @getVar "mobileScalePad"
      else
        @getVar "mobileScale"

    setMobileScale: (factor) ->
      if isIpad
        @setVar "mobileScalePad", factor
      else
        @setVar "mobileScale", factor

    scaledTxt: (txt) ->
      pattern = /font-size: *(\d+)px/g
      factor = @getMobileScale()
      unless factor
        if isIpad
          factor = 1
        else
          factor = 0.7
        @setMobileScale factor
      txt.replace pattern, (match, group) ->
        match.replace /\d+/, group * factor

    scaledCSS: ->
      @scaledTxt @css

    getCardStats: (card) ->
      c = card
      fmt = anki.utils.fmtTimeSpan
      lines = []
      addLine = (k, v) ->
        lines.push [ k, v ]

      strTime = (tm) ->
        "{0} ago".format anki.utils.fmtTimeSpan(anki.utils.time() - tm)

      addLine "Added", strTime(c.created)
      addLine "First Review", strTime(c.firstAnswered)  if c.firstAnswered
      addLine "Changed", strTime(c.modified)

      if c.reps
        next = anki.utils.time() - c.combinedDue
        if next > 0
          next = fmt(next) + " ago"
        else
          next = "in " + fmt(Math.abs(next))
        addLine "Due", next
      addLine "Interval", fmt(c.interval * 86400)
      addLine "Ease", c.factor.toFixed(2)
      if c.lastDue
        last = "{0} ago".format(fmt(anki.utils.time() - c.lastDue))
        addLine "Last Due", last
      unless c.interval == c.lastInterval
        addLine "Last Interval", fmt(c.lastInterval * 86400)
        addLine "Last Ease", c.lastFactor.toFixed(2)
      addLine "Reviews", "{0}/{1} (s={2})".format(c.yesCount, c.reps, c.successive)  if c.reps
      avg = fmt(c.averageTime, null, 2)
      addLine "Average Time", avg
      total = fmt(c.reviewTime, null, 2)
      addLine "Total Time", total
      addLine "Tags", @db.scalar("select tags from facts where id=?", c.factId) or "<none>"
      lines

    getDeckStats: ->
      return null  unless @cardCount
      that = this
      d = this
      all = []
      c = [ "General Statistics" ]
      fmtFloat = anki.utils.fmtFloat
      fmtTimeSpan = anki.utils.fmtTimeSpan
      c.push [ "Deck Created", d.createdTimeStr() + " ago" ]
      total = d.cardCount
      new_ = d.newCountAll()
      young = d.youngCardCount()
      old = d.matureCardCount()
      newP = fmtFloat(new_ / total * 100)
      youngP = fmtFloat(young / total * 100)
      oldP = fmtFloat(old / total * 100)
      c.push [ "Total Cards / Facts", "{0} / {1}".format(total, d.factCount) ]
      all.push c
      c = [ "Today" ]
      c.push [ "Average Answer Time", "{0} seconds".format(fmtFloat(d._dailyStats.averageTime)) ]
      c.push [ "Total Answer Time", fmtTimeSpan(d._dailyStats.reviewTime) ]
      ret = @correctAnswerStatsToday("mature")
      ret2 = @correctAnswerStatsToday("young")
      ret3 = @correctAnswerStatsToday("new")
      totalYes = ret[1] + ret2[1] + ret3[1]
      totalTotal = ret[2] + ret2[2] + ret3[2]
      totalP = fmtFloat(totalYes / totalTotal * 100)
      c.push [ "Correct Total", "{0}% ({1} of {2})".format(totalP, totalYes, totalTotal) ]
      c.push [ "Correct Mature", "{0}% ({1} of {2})".format(ret[0], ret[1], ret[2]) ]
      all.push c
      c = [ "Deck Life" ]
      c.push [ "Average Answer Time", "{0} seconds".format(fmtFloat(d._globalStats.averageTime)) ]
      c.push [ "Total Answer Time", "{0} hours".format(fmtFloat(d._globalStats.reviewTime / 3600)) ]
      ret = @correctAnswerStats("mature")
      c.push [ "Correct Mature", "{0}% ({1} of {2})".format(ret[0], ret[1], ret[2]) ]
      ret2 = @correctAnswerStats("young")
      c.push [ "Correct Young", "{0}% ({1} of {2})".format(ret2[0], ret2[1], ret2[2]) ]
      ret3 = @correctAnswerStats("new")
      c.push [ "Correct First-seen", "{0}% ({1} of {2})".format(ret3[0], ret3[1], ret3[2]) ]
      totalYes = ret[1] + ret2[1] + ret3[1]
      totalTotal = ret[2] + ret2[2] + ret3[2]
      totalP = fmtFloat(totalYes / totalTotal * 100)
      c.push [ "Correct Total", "{0}% ({1} of {2})".format(totalP, totalYes, totalTotal) ]
      all.push c
      c = [ "Card Maturity" ]
      c.push [ "Mature Cards", "{0} ({1}%)".format(old, oldP) ]
      c.push [ "Young Cards", "{0} ({1}%)".format(young, youngP) ]
      c.push [ "New Cards", "{0} ({1}%)".format(new_, newP) ]
      avgInt = @getAverageInterval()
      c.push [ "Average Interval", fmtTimeSpan(avgInt * 86400) ]  if avgInt
      all.push c
      existing = d.cardCount - d.newCountToday
      if existing and avgInt
        c = [ "Recent Work" ]
        repsPerDay = (start) ->
          "{0} reps / {1} days".format that.getRepsDone(start, 0), that.getDaysReviewed(start, 0)

        c.push [ "In Last Week", repsPerDay(-7) ]
        c.push [ "In Last Month", repsPerDay(-30) ]
        c.push [ "In Last 3 Months", repsPerDay(-92) ]
        c.push [ "In Last 6 Months", repsPerDay(-182) ]
        c.push [ "In Last Year", repsPerDay(-365) ]
        c.push [ "Over Deck Life", repsPerDay(-13000) ]
        all.push c
        c = [ "Average Daily Reviews" ]
        avgDay = (cnt) ->
          fmtFloat(cnt) + " cards/day"

        c.push [ "Over Deck Life", avgDay(@getSumInverseRoundInterval()) ]
        c.push [ "In Next Week", avgDay(@getWorkloadPeriod(7)) ]
        c.push [ "In Next Month", avgDay(@getWorkloadPeriod(30)) ]
        c.push [ "In Last Week", avgDay(@getPastWorkloadPeriod(7)) ]
        c.push [ "In Last Month", avgDay(@getPastWorkloadPeriod(30)) ]
        c.push [ "In Last 3 Months", avgDay(@getPastWorkloadPeriod(92)) ]
        c.push [ "In Last 6 Months", avgDay(@getPastWorkloadPeriod(182)) ]
        c.push [ "In Last Year", avgDay(@getPastWorkloadPeriod(365)) ]
        all.push c
        c = [ "Average Added" ]
        avgAdded = (cnt) ->
          np = that.getNewPeriod(cnt)
          "{0} ({1}/day)".format np, fmtFloat(np / cnt)

        newAvg = @newAverage()
        c.push [ "Over Deck Life", "{0}/day, {1}/mon".format(fmtFloat(newAvg), fmtFloat(newAvg * 30)) ]
        c.push [ "In Last Week", avgAdded(7) ]
        c.push [ "In Last Month", avgAdded(30) ]
        c.push [ "In Last 3 Months", avgAdded(92) ]
        c.push [ "In Last 6 Months", avgAdded(182) ]
        c.push [ "In Last Year", avgAdded(365) ]
        all.push c
        c = [ "Average First Seen" ]
        avgFirst = (cnt) ->
          np = that.getFirstPeriod(cnt)
          "{0} ({1}/day)".format np, fmtFloat(np / cnt)

        c.push [ "In Last Week", avgFirst(7) ]
        c.push [ "In Last Month", avgFirst(30) ]
        c.push [ "In Last 3 Months", avgFirst(92) ]
        c.push [ "In Last 6 Months", avgFirst(182) ]
        c.push [ "In Last Year", avgFirst(365) ]
        all.push c
        c = [ "Card Eases" ]
        c.push [ "Lowest Factor", d.db.scalar("select min(factor) from cards").toFixed(2) ]
        c.push [ "Average Factor", d.db.scalar("select avg(factor) from cards").toFixed(2) ]
        c.push [ "Highest Factor", d.db.scalar("select max(factor) from cards").toFixed(2) ]
        all.push c
      all

    correctAnswerStats: (type, obj) ->
      obj = @_globalStats  unless obj
      _no = (obj[type + "Ease0"] + obj[type + "Ease1"])
      _yes = (obj[type + "Ease2"] + obj[type + "Ease3"] + obj[type + "Ease4"])
      total = _no + _yes
      [ anki.utils.fmtFloat(_yes / total * 100), _yes, total ]

    correctAnswerStatsToday: (type) ->
      @correctAnswerStats type, @_dailyStats

    getAverageInterval: ->
      @db.scalar("select sum(interval) / count(interval) from cards " + "where cards.reps > 0") or 0

    createdTimeStr: ->
      anki.utils.fmtTimeSpan anki.utils.time() - @created

    getDaysReviewed: (start, finish) ->
      now = new Date().getTime() - (@utcOffset * 1000)
      x = new Date(now + start * 86400000)
      y = new Date(now + finish * 86400000)
      @db.scalar "select count() from stats where " + "day >= ? and day <= ? and reps > 0", anki.utils.jsDateToStr(x), anki.utils.jsDateToStr(y)

    getRepsDone: (start, finish) ->
      now = anki.utils.time()
      x = now + start * 86400
      y = now + finish * 86400
      @db.scalar "select count() from reviewHistory where " + "time >= ? and time <= ?", x, y

    getSumInverseRoundInterval: ->
      @db.scalar("select sum(1/round(max(interval, 1)+0.5)) from cards " + "where cards.reps > 0 and priority > 0") or 0

    getWorkloadPeriod: (period) ->
      cutoff = anki.utils.time() + 86400 * period
      (@db.scalar("select count(id) from cards " + "where combinedDue < ? and priority > 0 and " + "relativeDelay between 0 and 1", cutoff) or 0) / period

    getPastWorkloadPeriod: (period) ->
      cutoff = anki.utils.time() - 86400 * period
      (@db.scalar("select count(*) from reviewHistory " + "where time > ?", cutoff) or 0) / period

    newAverage: ->
      @cardCount / Math.max(1, @ageInDays())

    ageInDays: ->
      (anki.utils.time() - @created) / 86400

    getNewPeriod: (period) ->
      cutoff = anki.utils.time() - 86400 * period
      @db.scalar("select count(id) from cards where created > ?", cutoff) or 0

    getFirstPeriod: (period) ->
      cutoff = anki.utils.time() - 86400 * period
      @db.scalar("select count(*) from reviewHistory where reps = 1 " + "and time > ?", cutoff) or 0

    mungeFurigana: (txt, type) ->
      furiType = @furiganaType
      replNoSounds = (new_) ->
        (match, g1, g2) ->
          return match  if /^sound:/.test(g2)
          new_.format g1, g2

      rubify = (txt, type) ->
        if type == "question" and furiType == "3"
          txt = txt.replace(RegExp(" ?([^ >]+?)\\[(.+?)\\]", "g"), replNoSounds("<span class=tip>{0}<span>{1}</span></span>"))
        else
          txt = txt.replace(RegExp(" ?([^ >]+?)\\[(.+?)\\]", "g"), replNoSounds("<span class=\"ezRuby\" title=\"{1}\">{0}</span>"))
        txt

      removeKanji = (txt, type) ->
        repl = (match, g1, g2) ->
          return match  if /^sound:/.test(g2)
          g2

        txt = txt.replace(RegExp(" ?([^ >]+?)\\[(.+?)\\]", "g"), repl)
        txt

      if furiType == "1" and type == "question"
        fn = removeKanji
      else
        fn = rubify
      ret = fn(txt, type)
      ret

    furiganaCSS: ->
      "html>/* */body .ezRuby {\n" + "line-height: 1;\n" + "text-align: center;\n" + "white-space: nowrap;\n" + "vertical-align: baseline;\n" + "margin: 0;\n" + "padding: 0;\n" + "border: none;\n" + "display: inline-block;\n" + "}\n" + "html>/* */body .ezRuby:before {\n" + "font-size: 0.45em;\n" + "font-weight: normal;\n" + "line-height: 1.2;\n" + "text-decoration: none;\n" + "display: block;\n" + "content: attr(title);\n" + "}\n" + ".tip { position: relative; }\n" + ".tip:hover { background: #77f; color: #fff; }\n" + ".tip span { display: none; position: absolute; " + "top: -30px; width: 100px;" + "padding: 5px; z-index: 100; background: #000;" + "color: #fff; font-size: 14px; }\n" + "span:hover.tip span { display: block; }"

    getVar: (key) ->
      @db.scalar "select value from deckVars where key=?", key

    getBool: (key) ->
      @getInt key

    getInt: (key) ->
      res = @getVar(key)
      Number res

    getFloat: (key) ->
      @getInt key

    setVar: (key, val, mod) ->
      mod = true  if typeof mod == "undefined"
      return  if @db.scalar("select value=? from deckVars where key=?", val, key)
      @db.execute "insert or replace into deckVars (key, value) " + "values (?, ?)", key, val

    findFacts: (txt) ->
      txt = txt.replace(/\*/g, "%")
      @db.all "select distinct 'i'||factId, value from fields where " + "value like ? limit 100", "%" + txt + "%"

    setFailedCardPolicy: (idx) ->
      return  if idx == 5
      @collapseTime = 0
      @failedCardMax = 0

      if idx == 0
        d = 600
        @collapseTime = 1
        @failedCardMax = 20
      else if idx == 1
        d = 0
      else if idx == 2
        d = 600
      else if idx == 3
        d = 28800
      else d = 259200  if idx == 4
      @delay0 = d
      @delay1 = d
      @setModified()
      @toDB()

    getFailedCardPolicy: ->
      return 5  if @delay0 != @delay1
      d = @delay0
      if @collapseTime == 1
        return 0  if d == 600 and @failedCardMax == 20
        return 5
      if d == 0
        return 1
      else if d == 600
        return 2
      else if d == 28800
        return 3
      else return 4  if d == 259200
      5

  deck.init()
  deck

anki.updateCheck = (nid, cb) ->
  gid = Ti.Utils.md5HexDigest(Ti.Platform.macaddress)
  mod = Ti.Platform.model
  osver = Ti.Platform.version
  anki.utils.getJSON UPDATE_URL + "mobileVersion",
    nid: nid
    gid: gid
    mod: mod
    osver: osver
    ver: VERSION
  , cb, ->

anki.createSyncer = (deck, user, pass) ->
  syncer =
    KEYS: [ "models", "facts", "cards", "media" ]
    deck: deck
    user: user
    pass: pass
    stateChange: (txt) ->
      debug "state changed to: " + txt

    checkDecks: (next) ->
      that = this
      anki.utils.getJSON SYNC_URL + "getDecks",
        u: @user
        p: @pass
        pversion: "5"
        libanki: "1.2"
        nocomp: "1"
        client: VERSION
      , next

    prepareSync: (obj, forceDirection) ->
      that = this

      timediff = Math.abs(obj.timestamp - anki.utils.time())
      @timestamp = obj.timestamp
      if forceDirection
        @deck.close()
        if forceDirection == "keepLocal"
          @uploadDeck @deck.name
        else @downloadDeck @deck.name  if forceDirection == "keepRemote"
        return
      for deck of obj.decks
        if obj.decks.hasOwnProperty(deck)
          if deck == @deck.name
            deckobj = obj.decks[deck]
            break
      if deckobj
        @localTime = Math.round(@deck.modified)
        @remoteTime = Math.round(deckobj[0])
        @localSync = Math.round(@deck.lastSync)
        @remoteSync = Math.round(deckobj[1])
        if @localTime == @remoteTime
          @stateChange "noChanges"
          return
        min = Math.min(Math.round(@localSync), Math.round(@remoteSync))
        if @localTime > min and @remoteTime > min
          @stateChange "conflictsDetected"
          return
        @lastSync = (Math.min(@localSync, @remoteSync)) - timediff - 10
        @stateChange "fetchSummary"
        anki.utils.getJSON SYNC_URL + "summary",
          u: @user
          p: @pass
          d: @deck.name
          lastSync: @lastSync.toString()
          nocomp: "1"
          v: "2"
        , (obj) ->
          that.genPayload obj
      else
        anki.utils.getJSON SYNC_URL + "createDeck",
          u: @user
          p: @pass
          name: @deck.name
          nocomp: "1"
        , (res) ->
          if res and res.status == "OK"
            that.prepareSync obj, "keepLocal"
          else
            that.stateChange "error"

    genPayload: (obj) ->
      that = this
      @stateChange "genLocalSummary"
      lsum = @summary()
      rsum = obj
      needFull = @needFullSync([ lsum, rsum ])
      if needFull
        @deck.close()
        if @localTime < @remoteTime
          @downloadDeck @deck.name
        else
          @uploadDeck @deck.name
        return
      @stateChange "diff"
      @deck.db.execute "begin"
      payload = {}

      i = 0
      while i < @KEYS.length
        key = @KEYS[i]
        diff = @diffSummary(lsum, rsum, key)
        payload["added-" + key] = @getObjsFromKey(diff[0], key)
        payload["deleted-" + key] = diff[1]
        payload["missing-" + key] = diff[2]
        @deleteObjsFromKey diff[3], key
        i++
      if @localTime > @remoteTime
        payload.stats = @bundleStats()
        payload.history = @bundleHistory()
        payload.sources = @bundleSources()
        payload.deck = @bundleDeck()
      @stateChange "sendPayload"
      anki.utils.getJSON SYNC_URL + "applyPayload",
        u: @user
        p: @pass
        d: @deck.name
        payload: JSON.stringify(payload)
        nocomp: "1"
        v: "2"
      , (obj) ->
        that.applyPayloadReply obj

    applyPayloadReply: (reply) ->
      that = this
      @stateChange "applyReply"

      i = 0
      while i < @KEYS.length
        key = @KEYS[i]
        k = "added-" + key
        @updateObjsFromKey reply[k], key  if reply[k]
        i++
      if reply.deck
        @updateDeck reply.deck
        @updateStats reply.stats
        @updateHistory reply.history
        @updateSources reply.sources  if reply.sources
      cardIds = []
      arr = reply["added-cards"]
      i = 0
      while i < arr.length
        cardIds.push arr[i][0]
        i++
      @deck.updateCardTags cardIds
      @rebuildPriorities cardIds
      @deck.toDB()
      unless @missingFacts() == 0
        @deck.db.execute "rollback"
        throw message: "Facts missing after sync. " + "Please run Tools>Advanced>Check DB on " + "the desktop client."
      anki.utils.getJSON SYNC_URL + "finish",
        u: @user
        p: @pass
        d: @deck.name
        nocomp: "1"
      , (obj) ->
        that.finish obj

    finish: (obj) ->
      ok = true
      if obj == "OK"
        @deck.db.execute "commit"
      else
        ok = false
      @deck.close()
      if ok
        @stateChange "finished"
        @downloadMissingMedia @deck.name
      else
        @stateChange "error"

    missingFacts: ->
      @deck.db.scalar "select count() from cards where factId " + "not in (select id from facts)"

    rebuildPriorities: (cardIds) ->
      @deck.updateAllPriorities true, false
      @deck.updatePriorities cardIds, false

    changedFacts: ->
      cnt = @updatedFactCount or 0
      if @localTime > @remoteTime
        msg = "{0} fact{1} to server"
      else
        msg = "{0} fact{1} from server"
      msg.format cnt, (if cnt != 1 then "s" else "")

    getObjsFromKey: (ids, key) ->
      this["get" + key.capitalize()] ids

    deleteObjsFromKey: (ids, key) ->
      this["delete" + key.capitalize()] ids

    updateObjsFromKey: (ids, key) ->
      this["update" + key.capitalize()] ids

    applyDict: (obj, dict) ->
      for k of dict
        obj[k] = dict[k]  if dict.hasOwnProperty(k)

    needFullSync: (sums) ->
      return true  if @deck.lastSync <= 0

      i = 0

      while i < 2
        sum = sums[i]
        for e of sum
          return true  if sum[e].length > 1000  if sum.hasOwnProperty(e)
        i++
      return true  if @deck.db.scalar("select count() from reviewHistory where time > ?", @lastSync) > 1000
      lastDay = anki.utils.jsDateToStr(new Date(Math.max(0, @lastSync - 60 * 60 * 24) * 1000))
      return true  if @deck.db.scalar("select count() from stats where day >= ?", lastDay) > 100
      false

    prepareFullSync: (name, type) ->
      if @deck
        @deck.modified = Math.min(@deck.modified, @timestamp)
        @deck.toDB()
        @deck.close()
      constr = SYNC_URL + "full" + type
      args =
        u: @user
        p: @pass
        nocomp: "1"
        d: name or @deck.name

      args.v = "2"
      [ "notused", [ constr, args ], name or @deck.path ]

    downloadDeck: (name, cb) ->
      ret = @prepareFullSync(name, "down")
      @fullSyncFromServer ret[1], ret[2], cb

    uploadDeck: (name, cb) ->
      ret = @prepareFullSync(name, "up")
      @fullSyncToServer ret[1], ret[2], cb

    fullSyncToServer: (con, name) ->
      that = this
      constr = con[0]
      args = con[1]
      @stateChange "fullSyncToServer"
      @stateChange "uploadStarted"
      currentXhr = Titanium.Network.createHTTPClient()
      currentXhr.setTimeout 60000
      currentXhr.onerror = (e) ->
        that.stateChange "uploadFinished"
        anki.app.activityOff()  if anki and anki.app
        alert2 "Connection Error", "Please try again."

      currentXhr.onload = (->
        ->
          m = @responseText.match(/^OK (.*)/)
          unless m
            anki.app.activityOff()  if anki and anki.app
            alert2 "Connection Error", @responseText
          else
            db = anki.utils.DB(Ti.Database.open(anki.deckManager._deckName(name)))
            db.execute "update decks set lastSync = ?", m[1]
            db.close()
          that.stateChange "uploadFinished"
      )()
      @lastProgressReal = -1
      currentXhr.onsendstream = (e) ->
        if (e.progress - that.lastProgressReal) > 0.1
          that.lastProgress = e.progress
          that.lastProgressReal = e.progress
          that.stateChange "downloadProgress"

      currentXhr.open "POST", constr
      args.deck = Titanium.Filesystem.getFile(anki.deckManager._deckPath(name)).blob
      currentXhr.send args

    fullSyncFromServer: (con, name) ->
      that = this
      constr = con[0]
      args = con[1]
      @stateChange "fullSyncFromServer"
      that.stateChange "downloadStarted"
      data =
        u: @user
        p: @pass
        d: name

      currentXhr = Titanium.Network.createHTTPClient()
      currentXhr.setTimeout 60000
      onErr = (e) ->
        anki.deckManager.removeDeck name, false
        that.stateChange "downloadFinished"
        anki.app.activityOff()  if anki and anki.app
        alert2 "Connection Error", "Please try again."

      currentXhr.onerror = onErr
      currentXhr.onload = ->
        unless @status == 200
          onErr()
        else
          that.stateChange "downloadFinished"
          that.downloadMissingMedia name

      @lastProgress = 0
      currentXhr.ondatastream = (e) ->
        if (e.progress - that.lastProgress) > 0.1
          that.lastProgress = e.progress
          that.stateChange "downloadProgress"

      currentXhr.open "POST", SYNC_URL + "fulldown4"
      currentXhrFile = anki.deckManager.deckFileClear(name)
      currentXhr.file = currentXhrFile
      currentXhr.send data

    summary: ->
      munge = (row) ->
        row[0] = dumb(row[0])
        row[1] = parseFloat(dumb(row[1]))
        row

      cards: anki.utils.filtermap(munge, @deck.db.all("select 'i'||id, 'i'||modified from cards where modified > ?", @lastSync))
      delcards: anki.utils.filtermap(munge, @deck.db.all("select 'i'||cardId, 'i'||deletedTime from cardsDeleted " + "where deletedTime > ?", @lastSync))
      facts: anki.utils.filtermap(munge, @deck.db.all("select 'i'||id, 'i'||modified from facts where modified > ?", @lastSync))
      delfacts: anki.utils.filtermap(munge, @deck.db.all("select 'i'||factId, 'i'||deletedTime from factsDeleted " + "where deletedTime > ?", @lastSync))
      models: anki.utils.filtermap(munge, @deck.db.all("select 'i'||id, 'i'||modified from models where modified > ?", @lastSync))
      delmodels: anki.utils.filtermap(munge, @deck.db.all("select 'i'||modelId, 'i'||deletedTime from modelsDeleted " + "where deletedTime > ?", @lastSync))
      media: anki.utils.filtermap(munge, @deck.db.all("select 'i'||id, 'i'||created from media where created > ?", @lastSync))
      delmedia: anki.utils.filtermap(munge, @deck.db.all("select 'i'||mediaId, 'i'||deletedTime from mediaDeleted " + "where deletedTime > ?", @lastSync))

    diffSummary: (localSummary, remoteSummary, key) ->
      lexists = localSummary[key]
      ldeleted = localSummary["del" + key]
      rexists = remoteSummary[key]
      rdeleted = remoteSummary["del" + key]
      ldeletedIds = anki.utils.dict(ldeleted)
      rdeletedIds = anki.utils.dict(rdeleted)
      locallyEdited = []
      locallyDeleted = []
      remotelyEdited = []
      remotelyDeleted = []
      ids = {}
      i = 0
      while i < rexists.length
        id = rexists[i][0]
        mod = Math.round(rexists[i][1])
        ids[id] = [ null, mod ]
        i++
      i = 0
      while i < rdeleted.length
        id = rdeleted[i][0]
        mod = Math.round(rdeleted[i][1])
        ids[id] = [ null, null ]
        i++
      i = 0
      while i < lexists.length
        id = lexists[i][0]
        mod = Math.round(lexists[i][1])
        if id of ids
          ids[id][0] = mod
        else
          ids[id] = [ mod, null ]
        i++
      i = 0
      while i < ldeleted.length
        id = ldeleted[i][0]
        mod = Math.round(ldeleted[i][1])
        if id of ids
          ids[id][0] = null
        else
          ids[id] = [ null, null ]
        i++

      for id of ids
        if ids.hasOwnProperty(id)
          localMod = ids[id][0]
          remoteMod = ids[id][1]
          if localMod and remoteMod
            if localMod < remoteMod
              remotelyEdited.push id
            else locallyEdited.push id  if localMod > remoteMod
          else if localMod and not remoteMod
            if not (id of rdeletedIds) or rdeletedIds[id] < localMod
              locallyEdited.push id
            else
              remotelyDeleted.push id
          else if remoteMod and not localMod
            if not (id of ldeletedIds) or ldeletedIds[id] < remoteMod
              remotelyEdited.push id
            else
              locallyDeleted.push id
          else
            if (id of ldeletedIds) and not (id of rdeletedIds)
              locallyDeleted.push id
            else remotelyDeleted.push id  if (id of rdeletedIds) and not (id of ldeletedIds)
      [ locallyEdited, locallyDeleted, remotelyEdited, remotelyDeleted ]

    getModels: (ids) ->
      that = this
      anki.utils.filtermap ((arg) ->
        that.bundleModel arg
      ), ids

    updateModels: (models) ->
      modIds = []
      i = 0

      while i < models.length
        obj = models[i]
        mod = @getModel(obj.id, true)
        modIds.push obj.id
        @applyDict mod, obj
        mod.toDB()
        seen = []
        j = 0
        while j < obj.fieldModels.length
          @updateFieldModel obj.fieldModels[j]
          seen.push obj.fieldModels[j].id
          j++
        @deck.db.execute "delete from fieldModels where modelId = ? and " + "id not in " + anki.utils.ids2str(seen)
        seen = []
        j = 0
        while j < obj.cardModels.length
          @updateCardModel obj.cardModels[j]
          seen.push obj.cardModels[j].id
          j++
        @deck.db.execute "delete from cardModels where modelId = ? and " + "id not in " + anki.utils.ids2str(seen)
        i++
      @deck.db.execute "delete from modelsDeleted where modelId in " + anki.utils.ids2str(modIds)

    getModel: (id, create) ->
      create = true  if typeof create == "undefined"
      have = @deck.hasModel(id)
      if not create and not have
        null
      else
        if have
          @deck.createModel id
        else
          m = @deck.createModel()
          @deck.db.execute "update models set id = ? where id = ?", id, m.id
          @deck.createModel id

    bundleModel: (id) ->
      mod = @deck.createModel(id)
      that = this
      mod.fieldModels = anki.utils.filtermap((x) ->
        that.bundleFieldModel x
      , @deck.db.column0d("select 'i'||id from fieldModels where modelId = ?", id))
      mod.cardModels = anki.utils.filtermap((x) ->
        that.bundleCardModel x
      , @deck.db.column0d("select 'i'||id from cardModels where modelId = ?", id))
      delete mod.deck

      mod

    bundleFieldModel: (modelId) ->
      fm = @deck.db.first("select 'i'||id, ordinal,modelId,name,description, features," + "required,'unique',numeric,quizFontFamily,quizFontSize," + "quizFontColour,editFontFamily,editFontSize from " + "fieldModels where id = ?", modelId)
      id: dumb(fm[0])
      ordinal: fm[1]
      modelId: fm[2]
      name: fm[3]
      description: fm[4]
      features: fm[5]
      required: fm[6]
      unique: fm[7]
      numeric: fm[8]
      quizFontFamily: fm[9]
      quizFontSize: fm[10]
      quizFontColour: fm[11]
      editFontFamily: fm[12]
      editFontSize: fm[13]

    updateFieldModel: (obj) ->
      @deck.db.execute "insert into fieldModels values(?,0,0,'','','',0,0," + "0,null,null,null,null,0)", obj.id  unless @deck.db.scalar("select 1 from fieldModels where id = ?", obj.id)
      @deck.db.execute "update fieldModels set ordinal=?,modelId=?,name=?," + "description=?,features=?,required=?,'unique'=?," + "numeric=?,quizFontFamily=?,quizFontSize=?," + "quizFontColour=?,editFontFamily=?,editFontSize=? " + "where id=?", obj.ordinal, obj.modelId, obj.name, obj.description, obj.features, obj.required, obj.unique, obj.numeric, obj.quizFontFamily, obj.quizFontSize, obj.quizFontColour, obj.editFontFamily, obj.editFontSize, obj.id

    bundleCardModel: (modelId) ->
      fm = @deck.db.first("select 'i'||id, ordinal, modelId, name, description," + "active, qformat, aformat, lformat, qedformat," + "aedformat, questionInAnswer, questionFontFamily," + "questionFontSize, questionFontColour, questionAlign," + "answerFontFamily, answerFontSize, answerFontColour," + "answerAlign, lastFontFamily, lastFontSize, lastFontColour," + "editQuestionFontFamily, editQuestionFontSize, " + "editAnswerFontFamily, editAnswerFontSize, allowEmptyAnswer," + "typeAnswer from cardModels where id = ?", modelId)
      id: dumb(fm[0])
      ordinal: fm[1]
      modelId: fm[2]
      name: fm[3]
      description: fm[4]
      active: fm[5]
      qformat: fm[6]
      aformat: fm[7]
      lformat: fm[8]
      qedformat: fm[9]
      aedformat: fm[10]
      questionInAnswer: fm[11]
      questionFontFamily: fm[12]
      questionFontSize: fm[13]
      questionFontColour: fm[14]
      questionAlign: fm[15]
      answerFontFamily: fm[16]
      answerFontSize: fm[17]
      answerFontColour: fm[18]
      answerAlign: fm[19]
      lastFontFamily: fm[20]
      lastFontSize: fm[21]
      lastFontColour: fm[22]
      editQuestionFontFamily: fm[23]
      editQuestionFontSize: fm[24]
      editAnswerFontFamily: fm[25]
      editAnswerFontSize: fm[26]
      allowEmptyAnswer: fm[27]
      typeAnswer: fm[28]

    updateCardModel: (obj) ->
      @deck.db.execute "insert into cardModels values(?,0,0,'','',0,'','',null," + "null,null,0,'',0,'',0,'',0,'',0,'',0,'',null," + "null,null,null,0,'')", obj.id  unless @deck.db.scalar("select 1 from cardModels where id = ?", obj.id)
      @deck.db.execute "update cardModels set " + "ordinal=?, modelId=?, name=?, description=?," + "active=?, qformat=?, aformat=?, lformat=?, qedformat=?," + "aedformat=?, questionInAnswer=?, questionFontFamily=?," + "questionFontSize=?, questionFontColour=?, questionAlign=?," + "answerFontFamily=?, answerFontSize=?, answerFontColour=?," + "answerAlign=?, lastFontFamily=?, lastFontSize=?," + "lastFontColour=?, editQuestionFontFamily=?," + "editQuestionFontSize=?, editAnswerFontFamily=?," + "editAnswerFontSize=?, allowEmptyAnswer=?, typeAnswer=? " + "where id = ?", obj.ordinal, obj.modelId, obj.name, obj.description, obj.active, obj.qformat, obj.aformat, obj.lformat, obj.qedformat, obj.aedformat, obj.questionInAnswer, obj.questionFontFamily, obj.questionFontSize, obj.questionFontColour, obj.questionAlign, obj.answerFontFamily, obj.answerFontSize, obj.answerFontColour, obj.answerAlign, obj.lastFontFamily, obj.lastFontSize, obj.lastFontColour, obj.editQuestionFontFamily, obj.editQuestionFontSize, obj.editAnswerFontFamily, obj.editAnswerFontSize, obj.allowEmptyAnswer, obj.typeAnswer, obj.id

    deleteModels: (ids) ->
      i = 0
      while i < ids.length
        model = @getModel(id, false)
        @deck.deleteModel model  if model
        i++

    getFacts: (ids) ->
      factIds = anki.utils.ids2str(ids)
      facts = @deck.db.all("select 'i'||id,'i'||modelId,'i'||created,'i'||modified,tags," + "'i'||spaceUntil, 'i'||lastCardId from facts where id in " + factIds)
      fields = @deck.db.all("select 'i'||id,'i'||factId,'i'||fieldModelId,ordinal,value from " + "fields where factId in " + factIds)

      i = 0
      while i < facts.length
        facts[i][0] = dumb(facts[i][0])
        facts[i][1] = dumb(facts[i][1])
        facts[i][2] = parseFloat(dumb(facts[i][2]))
        facts[i][3] = parseFloat(dumb(facts[i][3]))
        facts[i][5] = parseFloat(dumb(facts[i][5]))
        facts[i][6] = dumb(facts[i][6])
        i++
      i = 0
      while i < fields.length
        fields[i][0] = dumb(fields[i][0])
        fields[i][1] = dumb(fields[i][1])
        fields[i][2] = dumb(fields[i][2])
        i++
      facts: facts
      fields: fields

    updateFacts: (factsdict) ->
      facts = factsdict.facts
      fields = factsdict.fields
      return  unless facts
      factIds = anki.utils.ids2str(anki.utils.filtermap((line) ->
        line[0]
      , facts))
      changed = (facts.length - @deck.db.scalar("select count(*) from facts where id in " + factIds))

      i = 0
      while i < facts.length
        t = facts[i]
        @deck.db.execute "insert or replace into facts (id, modelId, created, " + "modified, tags, spaceUntil, lastCardId) values " + "(?,?,?,?,?,?,?)", t[0], t[1], t[2], t[3], t[4], t[5], t[6]
        i++
      @deck.db.execute "delete from fields where factId in " + factIds
      i = 0
      while i < fields.length
        t = fields[i]
        @deck.db.execute "insert into fields (id, factId, fieldModelId, ordinal," + "value) values (?,?,?,?,?)", t[0], t[1], t[2], t[3], t[4]
        i++
      @deck.db.execute "delete from factsDeleted where factId in " + factIds

    deleteFacts: (ids) ->
      @deck.deleteFacts ids

    getCards: (ids) ->
      cards = @deck.db.all("select 'i'||id, 'i'||factId, 'i'||cardModelId, 'i'||created," + "'i'||modified, tags, ordinal, priority, interval, " + "lastInterval, 'i'||due, 'i'||lastDue, factor, " + "'i'||firstAnswered, reps, successive, averageTime," + "reviewTime, youngEase0, youngEase1, youngEase2, " + "youngEase3, youngEase4, matureEase0, matureEase1, " + "matureEase2, matureEase3, matureEase4, yesCount," + "noCount, question, answer, lastFactor, 'i'||spaceUntil," + "type, 'i'||combinedDue, relativeDelay from cards where id in " + anki.utils.ids2str(ids))
      facts = {}
      factsCnt = 0
      i = 0

      while i < cards.length
        unless facts[cards[i][1]]
          facts[cards[i][1]] = true
          factsCnt++
        cards[i][0] = dumb(cards[i][0])
        cards[i][1] = dumb(cards[i][1])
        cards[i][2] = dumb(cards[i][2])
        cards[i][3] = parseFloat(dumb(cards[i][3]))
        cards[i][4] = parseFloat(dumb(cards[i][4]))
        cards[i][10] = parseFloat(dumb(cards[i][10]))
        cards[i][11] = parseFloat(dumb(cards[i][11]))
        cards[i][13] = parseFloat(dumb(cards[i][13]))
        cards[i][33] = parseFloat(dumb(cards[i][33]))
        cards[i][35] = parseFloat(dumb(cards[i][35]))
        cards[i][36] = parseInt(cards[i][36], 10)
        i++
      @updatedFactCount = factsCnt  if @localTime > @remoteTime
      cards

    updateCards: (cards) ->
      return  unless cards
      getType = (row) ->
        return row[36]  if row.length > 36
        if row[15]
          return 1
        else return 0  if row[14]
        2

      cardIds = anki.utils.ids2str(anki.utils.filtermap((line) ->
        line[0]
      , cards))
      changed = cards.length - @deck.db.scalar("select count(*) from cards where id in " + cardIds)

      facts = {}
      factsCnt = 0
      i = 0

      while i < cards.length
        c = cards[i]
        unless facts[c[1]]
          facts[c[1]] = true
          factsCnt++
        @deck.db.execute "insert or replace into cards (id, factId, cardModelId," + "created, modified, tags, ordinal, priority, interval," + "lastInterval, due, lastDue, factor, firstAnswered," + "reps, successive, averageTime, reviewTime, youngEase0," + "youngEase1, youngEase2, youngEase3, youngEase4," + "matureEase0, matureEase1, matureEase2, matureEase3," + "matureEase4, yesCount, noCount, question, answer," + "lastFactor, spaceUntil, type, combinedDue, " + "relativeDelay, isDue) values (?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,0)", c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7], c[8], c[9], c[10], c[11], c[12], c[13], c[14], c[15], c[16], c[17], c[18], c[19], c[20], c[21], c[22], c[23], c[24], c[25], c[26], c[27], c[28], c[29], c[30], c[31], c[32], c[33], c[34], c[35], getType(c)
        i++
      @deck.db.execute "delete from cardsDeleted where cardId in " + cardIds
      @updatedFactCount = factsCnt  if @localTime < @remoteTime

    deleteCards: (ids) ->
      @deck.deleteCards ids

    bundleDeck: ->
      d = {}
      @deck.modified = Math.min(@timestamp, @deck.modified)
      @deck.lastSync = Math.max(anki.utils.time(), @deck.modified + 1)
      d.lastSync = @deck.lastSync
      d.id = @deck.id
      d.created = @deck.created
      d.modified = @deck.modified
      d.description = @deck.description
      d.currentModelId = @deck.currentModelId
      d.hardIntervalMin = @deck.hardIntervalMin
      d.hardIntervalMax = @deck.hardIntervalMax
      d.midIntervalMin = @deck.midIntervalMin
      d.midIntervalMax = @deck.midIntervalMax
      d.easyIntervalMin = @deck.easyIntervalMin
      d.easyIntervalMax = @deck.easyIntervalMax
      d.delay0 = @deck.delay0
      d.delay1 = @deck.delay1
      d.delay2 = @deck.delay2
      d.collapseTime = @deck.collapseTime
      d.highPriority = @deck.highPriority
      d.medPriority = @deck.medPriority
      d.lowPriority = @deck.lowPriority
      d.suspended = @deck.suspended
      d.newCardOrder = @deck.newCardOrder
      d.newCardSpacing = @deck.newCardSpacing
      d.failedCardMax = @deck.failedCardMax
      d.newCardsPerDay = @deck.newCardsPerDay
      d.sessionRepLimit = @deck.sessionRepLimit
      d.sessionTimeLimit = @deck.sessionTimeLimit
      d.utcOffset = @deck.utcOffset
      d.cardCount = @deck.cardCount
      d.factCount = @deck.factCount
      d.failedNowCount = @deck.failedNowCount
      d.failedSoonCount = @deck.failedSoonCount
      d.revCount = @deck.revCount
      d.newCount = @deck.newCount
      d.revCardOrder = @deck.revCardOrder
      d.meta = @deck.db.all("select * from deckVars")
      d

    updateDeck: (deck) ->
      meta = deck.meta
      i = 0

      while i < meta.length
        @deck.db.execute "insert or replace into deckVars " + "(key, value) values (?, ?)", meta[i][0], meta[i][1]
        i++
      delete deck.meta

      @applyDict @deck, deck
      @deck.toDB()
      @deck.updateDynamicIndices()

    bundleStats: ->
      that = this
      bundleStat = (statId) ->
        stat = that.deck.createStats(null, null, statId)
        stat.day = anki.utils.strToOrdinal(stat.day)
        delete stat.id

        delete stat.deck

        stat

      lastDay = anki.utils.jsDateToStr(new Date(Math.max(0, (@deck.lastSync - 60 * 60 * 24) * 1000)))
      ids = @deck.db.column0d("select 'i'||id from stats where type = 1 and day >= ?", lastDay)
      stats =
        global: bundleStat(1)
        daily: anki.utils.filtermap(bundleStat, ids)

      stats

    updateStats: (stats) ->
      that = this

      updateStat = (statobj) ->
        statobj.day = anki.utils.ordinalToStr(statobj.day)
        stat = that.deck.createStats(1, statobj.day)
        that.applyDict stat, statobj
        stat.toDB()

      stats.global.day = anki.utils.ordinalToStr(stats.global.day)
      @applyDict @deck._globalStats, stats.global
      @deck._globalStats.toDB()
      anki.utils.filtermap updateStat, stats.daily

    bundleHistory: ->
      hist = @deck.db.all("select 'i'||cardId, 'i'||time, lastInterval, nextInterval, ease," + "delay,lastFactor, nextFactor, reps, thinkingTime," + "yesCount, noCount from reviewHistory where time > ?", @lastSync)
      i = 0

      while i < hist.length
        hist[i][0] = dumb(hist[i][0])
        hist[i][1] = parseFloat(dumb(hist[i][1]))
        hist[i][2] = parseFloat(hist[i][2])
        hist[i][3] = parseFloat(hist[i][3])
        hist[i][4] = parseInt(hist[i][4], 10)
        hist[i][5] = parseFloat(hist[i][5])
        hist[i][6] = parseFloat(hist[i][6])
        hist[i][7] = parseFloat(hist[i][7])
        hist[i][8] = parseInt(hist[i][8], 10)
        hist[i][9] = parseFloat(hist[i][9])
        hist[i][10] = parseInt(hist[i][10], 10)
        hist[i][11] = parseInt(hist[i][11], 10)
        i++
      hist

    updateHistory: (history) ->
      i = 0

      while i < history.length
        h = history[i]
        @deck.db.execute "insert or ignore into reviewHistory " + "(cardId, time, lastInterval, nextInterval, ease, " + "delay, lastFactor, nextFactor, reps, thinkingTime," + "yesCount, noCount) values (?,?,?,?,?,?,?,?,?,?,?,?)", h[0], h[1], h[2], h[3], h[4], h[5], h[6], h[7], h[8], h[9], h[10], h[11]
        i++

    bundleSources: ->
      srcs = @deck.db.all("select 'i'||id, name, created, lastSync, syncPeriod from sources")
      i = 0

      while i < srcs.length
        srcs[i][0] = dumb(srcs[i][0])
        srcs[i][2] = parseFloat(srcs[i][2])
        srcs[i][3] = parseFloat(srcs[i][3])
        i++
      srcs

    updateSources: (srcs) ->
      i = 0

      while i < srcs.length
        h = srcs[i]
        @deck.db.execute "insert or ignore into sources " + "(id,name,created,lastSync,syncPeriod) " + "values (?,?,?,?,?)", h[0], h[1], h[2], h[3], h[4]
        i++

    getMedia: (ids) ->
      media = @deck.db.all("select 'i'||id, filename, size, 'i'||created, originalPath, " + "description from media where id in " + anki.utils.ids2str(ids))
      i = 0

      while i < media.length
        media[i][0] = dumb(media[i][0])
        media[i][2] = parseInt(media[i][2], 10)
        media[i][3] = parseFloat(dumb(media[i][3]))
        i++
      media

    updateMedia: (media) ->
      ids = []
      i = 0

      while i < media.length
        m = media[i]
        ids.push m[0]
        @deck.db.execute "insert or replace into media (id, filename, size," + "created, originalPath, description) values " + "(?,?,?,?,?,?)", m[0], m[1], m[2], m[3], m[4], m[5]
        i++
      @deck.db.execute "delete from mediaDeleted where mediaId in " + anki.utils.ids2str(ids)

    deleteMedia: (ids) ->
      sids = anki.utils.ids2str(ids)
      files = @deck.db.column0("select filename from media where id in " + sids)
      @deck.db.execute "insert into mediaDeleted select id, ? from media " + "where media.id in " + sids, anki.utils.time()
      @deck.db.execute "delete from media where id in " + sids

    downloadMissingMedia: (name, cb) ->
      unless Ti.App.Properties.getString("downloadMedia") == true
        @stateChange "noMedia"
        return
      try
        @deck = anki.deckManager.openDeck(name)
      catch err
        alert2 "Download Error", "Deck was corrupt. Please try again."
        anki.app.activityOff()  if anki and anki.app
        return
      @deck.db.execute "update decks set syncName = 1"
      url = @deck.getVar("mediaURL")
      unless url
        cb()  if cb
        @deck.close()
        @stateChange "noMedia"
        return
      @stateChange "mediaStart"
      @mediaCB = cb
      url = Ti.Network.decodeURIComponent(url)
      url = Ti.Network.encodeURIComponent(url)
      url = url.replace(/%3A/g, ":")
      url = url.replace(/%2F/g, "/")
      @mediaURL = url
      @mediaPending = @deck.db.column0("select filename from media where originalPath != ''")
      @mediaPending = @mediaPending.concat(@deck.fontFiles())
      @mediaCount = 0
      @checkNextMedia()

    checkNextMedia: ->
      that = this

      startTime = anki.utils.time()
      @stateChange "fetchMedia"
      fn2 = ->
        that.checkNextMedia()

      while true
        if (anki.utils.time() - startTime) > 0.8
          @stateChange "fetchMedia"
          setTimeout fn2, 50
          return
        unless @mediaPending.length
          @mediaCB()  if @mediaCB
          @deck.close()
          @stateChange "finishedMedia"
          return
        fn = @mediaPending.pop()
        have = anki.deckManager.haveMediaForDeck(@deck.name, fn)
        continue  if have
        break
      url = @mediaURL + Ti.Network.encodeURIComponent(fn)
      currentXhr = Titanium.Network.createHTTPClient()
      currentXhr.setTimeout 60000
      onErr = ->
        currentXhrFile.deleteFile()
        that.deck.close()
        that.failedURL = url

      currentXhr.onerror = (e) ->
        onErr()
        alert2 "Connection Error", "Couldn't fetch " + url
        that.deck.close()
        that.stateChange "mediaError"

      currentXhr.onload = ->
        try
          if @status == 200
            type = @getResponseHeader("Content-Type")
            if /html/i.test(type) or /text/i.test(type)
              onErr()
              that.stateChange "invalidMedia"
              return
            that.mediaCount++
            that.checkNextMedia()
          else if @status >= 500
            onErr()
            that.stateChange "failedMedia"
          else
            onErr()
            that.stateChange "missingMedia"
        catch err
          alert err

      currentXhr.open "GET", url
      currentXhrFile = anki.deckManager._mediaFile(that.deck.name, fn)
      currentXhr.file = currentXhrFile
      currentXhr.send()

  syncer

anki.deckManager =
  db: null
  decks: []
  decksByName: {}
  init: ->
    @db = anki.utils.DB(Ti.Database.install("anki.db.template", "decks.db"))
    @updateDecks()

  updateDecks: ->
    @decks = @db.all("select name, lastOpened, file from decks order by name")
    @decksByName = {}
    i = 0

    while i < @decks.length
      @decksByName[@decks[i][0]] = @decks[i]
      i++
    @decks

  deckNames: ->
    decks = @updateDecks()
    ret = []
    i = 0

    while i < decks.length
      ret.push decks[i][0]
      i++
    ret

  hasDeck: (name) ->
    name of @decksByName

  createDeck: (name) ->
    assert false
    assert not @hasDeck(name)
    deck = Ti.Database.install("nt.anki.mp3", name)
    deck.close()
    @_addDeckRecord name

  _addDeckRecord: (name) ->
    @db.execute "insert into decks (name, lastOpened, file) values " + "(?, 0, ?)", name, anki.utils.genID()
    @updateDecks()

  removeDeck: (name, delMedia) ->
    path = @_deckPath(name)

    delMedia = true  if typeof delMedia == "undefined"
    if path
      file = Titanium.Filesystem.getFile(path)
      file.deleteFile()  if file.exists()
    if delMedia
      file = @_deckMediaDirFile(name)
      file.deleteDirectory true
    @db.execute "delete from decks where name = ?", name
    @updateDecks()

  _deckName: (name) ->
    @decksByName[name][2]

  _deckPath: (name) ->
    return null  unless (name of @decksByName)
    real = @_deckName(name)
    path = DBPATH + "/" + real
    path += ".sql"
    path

  _deckMediaDirFile: (name) ->
    path = MEDIAPATH + "/" + name
    file = Titanium.Filesystem.getFile(MEDIAPATH, name)
    file.createDirectory()  unless file.exists()
    file

  _mediaFile: (deckName, mediaName) ->
    file = @_deckMediaDirFile(deckName)
    f2 = Titanium.Filesystem.getFile(file.getNativePath(), mediaName)
    f2

  updateDeckFromFile: (name, blob) ->
    file = @deckFileClear(name)
    file.write blob
    file

  deckFileClear: (name) ->
    @removeDeck name, false
    @_addDeckRecord name
    path = @_deckPath(name)
    file = Titanium.Filesystem.getFile(path)
    file

  updateMediaFromFile: (deckName, mediaName, blob) ->
    file = @_mediaFile(deckName, mediaName)
    file.write blob

  importMediaFromDir: (deckName, dirfile) ->
    files = dirfile.getDirectoryListing()

    i = 0

    while i < files.length
      fhand = Ti.Filesystem.getFile(dirfile.getNativePath(), files[i])
      @updateMediaFromFile deckName, files[i], fhand
      fhand.deleteFile()
      i++

  haveMediaForDeck: (deckName, mediaName) ->
    file = @_mediaFile(deckName, mediaName)
    file.exists()

  openDeck: (name, fast) ->
    @currentDeck = name
    assert @hasDeck(name)
    deck = anki.openDeck(@_deckName(name), name, false, fast)
    @db.execute "update decks set lastOpened = ? where name = ?", anki.utils.time(), name
    deck

anki.deckManager.init()
