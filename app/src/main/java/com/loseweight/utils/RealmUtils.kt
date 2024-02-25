package com.loseweight.utils

class RealmUtils {

    /*fun updateOfflineUserList(
        data: List<UserListData.UserList>?,
        myRealm: Realm
    ) {
        if (data != null && data.size > 0) {

            myRealm.beginTransaction()
            myRealm.delete(UserListRealmData::class.java)
            myRealm.commitTransaction()

            for (item in data) {
                val conversation: UserListRealmData? = myRealm.where<UserListRealmData>(
                    UserListRealmData::class.java
                ).equalTo("userId", item.userId).findFirst()
                if (conversation == null) {
                    addOfflineUserToRealm(item, myRealm)
                } else {
                    myRealm.beginTransaction()
                    *//*conversation.setFileUrl(item.fileUrl)
                    conversation.setRes(item.isRes)
                    conversation.setMsgStatus(item.msgStatus)*//*
                    myRealm.commitTransaction()
                }
            }
        }
    }

    fun addOfflineUserToRealm(
        data: UserListData.UserList,
        myRealm: Realm
    ) {
        myRealm.beginTransaction()
        val conversation: UserListRealmData = myRealm.createObject<UserListRealmData>(
            UserListRealmData::class.java
        )
        conversation.userId = data.userId
        conversation.firstName = data.firstName
        conversation.middleName = data.middleName
        conversation.lastName = data.lastName
        conversation.displayName = data.displayName
        conversation.email = data.email
        conversation.userName = data.userName
        conversation.isActive = data.isActive
        conversation.mobile = data.mobile
        conversation.initial = data.initial
        conversation.userRoleId = data.userRoleId
        conversation.roleName = data.roleName
        conversation.facilityId = data.facilityId
        conversation.facilityName = data.facilityName
        conversation.facilityCode = data.facilityCode

        myRealm.commitTransaction()
    }

    fun getOfflineUserList(myRealm: Realm, facilityCode: String?): List<UserListData.UserList> {
        var userList = arrayListOf<UserListData.UserList>()
        var results: RealmResults<UserListRealmData> =
            myRealm.where(UserListRealmData::class.java).equalTo("facilityCode", facilityCode)
                .findAll()
        if (!myRealm.isInTransaction()) myRealm.beginTransaction()
//        results = results.sort("microtime", Sort.DESCENDING)
        val items: Array<UserListRealmData> = results.toTypedArray<UserListRealmData>()

        for (data in items) {
            val userData = UserListData.UserList(
                data.userId,
                data.firstName,
                data.middleName,
                data.lastName,
                data.userName,
                data.isActive,
                data.email,
                data.mobile,
                data.initial,
                data.displayName,
                data.userRoleId,
                data.roleName,
                data.facilityId,
                data.facilityName,
                data.facilityCode
            )
            userList.add(userData)
        }

//        if(results.size()>0)
//            id = myRealm.where(PersonDetailsModel.class).max("id").intValue() + 1;
        //        if(results.size()>0)
//            id = myRealm.where(PersonDetailsModel.class).max("id").intValue() + 1;
        myRealm.commitTransaction()
        return userList

    }

    class storeMasterDataOffline(masterData: MasterData.MasterData_a, obj: RealmUtils) :
        AsyncTask<Void, Void, String>() {

        val masterData = masterData
        val obj = obj

        override fun doInBackground(vararg params: Void?): String? {

            obj.storeMasterData(Realm.getDefaultInstance(), masterData)

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()

            // ...
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // ...
        }
    }

    fun storeMasterData(myRealm: Realm, masterData: MasterData.MasterData_a) {

        Debug.e("storeMasterDataOffline", "started =====>")

        if (masterData.area.isNullOrEmpty().not()) {
            updateOfflineAreaList(masterData.area, myRealm)
        }

        if (masterData.cellActivityGroups.isNullOrEmpty().not()) {
            myRealm.beginTransaction()
            myRealm.delete(CellActivityRealmData::class.java)
            myRealm.commitTransaction()
            for (group in masterData.cellActivityGroups!!) {
                updateCellActivityList(group.activityGroupID, group.activities, myRealm)
            }
        }

        if (masterData.inmateActivityGroups.isNullOrEmpty().not()) {
            myRealm.beginTransaction()
            myRealm.delete(InmateActivityRealmData::class.java)
            myRealm.commitTransaction()
            for (group in masterData.inmateActivityGroups!!) {
                updateInmateActivityList(group.activityGroupID, group.activities, myRealm)
            }
        }

        if (masterData.masterBeaconsCellsInmates != null && masterData.masterBeaconsCellsInmates!!.masterBeacons.isNullOrEmpty().not()) {

            myRealm.beginTransaction()
            myRealm.delete(MasterCellRealmData::class.java)
            myRealm.commitTransaction()

            myRealm.beginTransaction()
            myRealm.delete(MasterInmateRealmData::class.java)
            myRealm.commitTransaction()

            for (masterBeacon in masterData.masterBeaconsCellsInmates!!.masterBeacons!!) {
                if (masterBeacon.masterCells.isNullOrEmpty().not()) {

                    updateBeaconCellList(masterBeacon, masterBeacon.masterCells, myRealm)

                }
            }
        }


    }

    private fun updateOfflineAreaList(area: List<MasterData.Area>?, myRealm: Realm) {
        if (area != null && area.size > 0) {
            for (item in area) {

                myRealm.beginTransaction()
                myRealm.delete(AreaRealmData::class.java)
                myRealm.commitTransaction()

                val areaData: AreaRealmData? = myRealm.where<AreaRealmData>(
                    AreaRealmData::class.java
                ).equalTo("areaId", item.areaId).findFirst()
                if (areaData == null) {
                    addAreaToRealm(item, myRealm)
                } else {
                    myRealm.beginTransaction()
                    areaData.areaName = item.areaName
                    myRealm.commitTransaction()
                }
            }
        }
    }

    fun addAreaToRealm(data: MasterData.Area, myRealm: Realm) {

        myRealm.beginTransaction()

        val area: AreaRealmData = myRealm.createObject<AreaRealmData>(AreaRealmData::class.java)
        area.areaId = data.areaId
        area.areaName = data.areaName

        myRealm.commitTransaction()
    }

    private fun updateCellActivityList(
        activityGroupID: Int,
        cellActivities: List<MasterData.Activity>?,
        myRealm: Realm
    ) {
        if (cellActivities != null && cellActivities.size > 0) {

            for (item in cellActivities) {
                val cellActivity: CellActivityRealmData? =
                    myRealm.where<CellActivityRealmData>(CellActivityRealmData::class.java)
                        .equalTo("activityId", item.activityId).findFirst()

                if (cellActivity == null) {
                    addCellActivityToRealm(item, activityGroupID, myRealm)
                } else {
                    myRealm.beginTransaction()
                    cellActivity.activityName = item.activityName
                    myRealm.commitTransaction()
                }
            }
        }
    }

    fun addCellActivityToRealm(data: MasterData.Activity, activityGroupID: Int, myRealm: Realm) {

        myRealm.beginTransaction()

        val area: CellActivityRealmData =
            myRealm.createObject<CellActivityRealmData>(CellActivityRealmData::class.java)
        area.activityGroupID = activityGroupID
        area.activityId = data.activityId
        area.activityName = data.activityName

        myRealm.commitTransaction()
    }

    fun getCellActivityList(myRealm: Realm): List<MasterData.Activity> {
        var cellActivities = arrayListOf<MasterData.Activity>()
        var results: RealmResults<CellActivityRealmData> =
            myRealm.where(CellActivityRealmData::class.java).findAll()

        if (!myRealm.isInTransaction())
            myRealm.beginTransaction()
//        results = results.sort("microtime", Sort.DESCENDING)
        val items: Array<CellActivityRealmData> = results.toTypedArray<CellActivityRealmData>()

        for (data in items) {
            cellActivities.add(
                MasterData.Activity(
                    data.activityGroupID,
                    data.activityId,
                    data.activityName
                )
            )
        }

        myRealm.commitTransaction()
        return cellActivities

    }

    private fun updateInmateActivityList(
        activityGroupID: Int,
        inmateActivities: List<MasterData.Activity_>?,
        myRealm: Realm
    ) {
        if (inmateActivities != null && inmateActivities.size > 0) {



            for (item in inmateActivities) {
                val inamteActivity: InmateActivityRealmData? =
                    myRealm.where<InmateActivityRealmData>(InmateActivityRealmData::class.java)
                        .equalTo("activityId", item.activityId).findFirst()

                if (inamteActivity == null) {
                    addInmateActivityToRealm(item, activityGroupID, myRealm)
                } else {
                    myRealm.beginTransaction()
                    inamteActivity.activityName = item.activityName
                    myRealm.commitTransaction()
                }
            }
        }
    }

    fun addInmateActivityToRealm(data: MasterData.Activity_, activityGroupID: Int, myRealm: Realm) {

        myRealm.beginTransaction()

        val area: InmateActivityRealmData =
            myRealm.createObject<InmateActivityRealmData>(InmateActivityRealmData::class.java)

        area.activityGroupID = activityGroupID
        area.activityId = data.activityId
        area.activityName = data.activityName

        myRealm.commitTransaction()
    }

    fun getInmateActivityList(myRealm: Realm): List<MasterData.Activity_> {
        var inmateActivities = arrayListOf<MasterData.Activity_>()
        var results: RealmResults<InmateActivityRealmData> =
            myRealm.where(InmateActivityRealmData::class.java).findAll()

        if (!myRealm.isInTransaction())
            myRealm.beginTransaction()
//        results = results.sort("microtime", Sort.DESCENDING)
        val items: Array<InmateActivityRealmData> = results.toTypedArray<InmateActivityRealmData>()

        for (data in items) {
            inmateActivities.add(
                MasterData.Activity_(
                    data.activityGroupID,
                    data.activityId,
                    data.activityName
                )
            )
        }

        myRealm.commitTransaction()
        return inmateActivities

    }


    private fun updateBeaconCellList(
        masterBeacondata: MasterData.MasterBeacon,
        cellList: List<MasterData.MasterCell>?,
        myRealm: Realm
    ) {
        if (cellList != null && cellList.size > 0) {
            for (item in cellList) {
                val cell: MasterCellRealmData? =
                    myRealm.where<MasterCellRealmData>(MasterCellRealmData::class.java)
                        .equalTo("cellId", item.cellId).equalTo("beaconId", item.beaconId)
                        .findFirst()

                if (cell == null) {
                    addBeaconCellToRealm(masterBeacondata, item, myRealm)
                } else {
                    myRealm.beginTransaction()
                    cell.beaconId = item.beaconId
                    cell.beaconName = masterBeacondata.beaconName
                    cell.majorId = masterBeacondata.majorId
                    cell.minorId = masterBeacondata.minorId
                    cell.areaId = masterBeacondata.areaId

                    cell.cellName = item.cellName
                    cell.inmatesCount = item.inmatesCount
                    cell.activityGroupId = item.activityGroupId
                    cell.inmateActivityGroupId = item.inmateActivityGroupId
                    cell.isGroupHead = item.isGroupHead
//                    cell.masterInmates = item.masterInmates

                    myRealm.commitTransaction()
                }
            }
        }
    }

    fun addBeaconCellToRealm(
        masterBeacondata: MasterData.MasterBeacon,
        item: MasterData.MasterCell,
        myRealm: Realm
    ) {

        myRealm.beginTransaction()

        val cell: MasterCellRealmData =
            myRealm.createObject<MasterCellRealmData>(MasterCellRealmData::class.java)

        cell.beaconId = item.beaconId
        cell.beaconName = masterBeacondata.beaconName
        cell.majorId = masterBeacondata.majorId
        cell.minorId = masterBeacondata.minorId
        cell.areaId = masterBeacondata.areaId

        cell.cellId = item.cellId
        cell.cellName = item.cellName
        cell.inmatesCount = item.inmatesCount
        cell.activityGroupId = item.activityGroupId
        cell.inmateActivityGroupId = item.inmateActivityGroupId
        cell.isGroupHead = item.isGroupHead
//        cell.masterInmates = item.masterInmates

        myRealm.commitTransaction()

        if (item.masterInmates.isNullOrEmpty().not()) {
            for (inmate in item.masterInmates!!) {

                if (inmate != null) {
                    val inmateRealmData: MasterInmateRealmData? =
                        myRealm.where<MasterInmateRealmData>(MasterInmateRealmData::class.java)
                            .equalTo("inmateId", inmate.inmateId)
                            .equalTo("cellId", inmate.cellId)
                            .equalTo("beaconId", inmate.beaconId)
                            .findFirst()

                    if (inmateRealmData == null) {
                        addInmateToRealm(inmate, myRealm, masterBeacondata)
                    } else {
                        myRealm.beginTransaction()
                        inmateRealmData.beaconId = item.beaconId
                        inmateRealmData.beaconName = masterBeacondata.beaconName
                        inmateRealmData.majorId = masterBeacondata.majorId
                        inmateRealmData.minorId = masterBeacondata.minorId
                        inmateRealmData.areaId = masterBeacondata.areaId

                        inmateRealmData.cellId = inmate.cellId
                        inmateRealmData.inmateName = inmate.inmateName
                        inmateRealmData.photoUrl = inmate.photoUrl

                        myRealm.commitTransaction()
                    }

                }

            }
        }
    }


    fun addInmateToRealm(
        inmate: MasterData.MasterInmate,
        myRealm: Realm,
        masterBeaconData: MasterData.MasterBeacon
    ) {

        myRealm.beginTransaction()

        val inmateRealmData: MasterInmateRealmData =
            myRealm.createObject<MasterInmateRealmData>(MasterInmateRealmData::class.java)

        inmateRealmData.beaconId = masterBeaconData.beaconId
        inmateRealmData.beaconName = masterBeaconData.beaconName
        inmateRealmData.majorId = masterBeaconData.majorId
        inmateRealmData.minorId = masterBeaconData.minorId
        inmateRealmData.areaId = masterBeaconData.areaId

        inmateRealmData.inmateId = inmate.inmateId
        inmateRealmData.cellId = inmate.cellId
        inmateRealmData.inmateName = inmate.inmateName
        inmateRealmData.photoUrl = inmate.photoUrl

        myRealm.commitTransaction()
    }


    fun getCellList(
        myRealm: Realm,
        beaconIds: Array<Int>?
    ): ArrayList<MasterData.MasterCell> {

        var cellList = arrayListOf<MasterData.MasterCell>()
        var results: RealmResults<MasterCellRealmData>? = null

        results = if (beaconIds != null) {
            myRealm.where(MasterCellRealmData::class.java).`in`("beaconId", beaconIds).findAll()
        } else {
            myRealm.where(MasterCellRealmData::class.java).findAll()
        }


        if (!myRealm.isInTransaction())
            myRealm.beginTransaction()

        val items: Array<MasterCellRealmData> = results.toTypedArray<MasterCellRealmData>()

        for (data in items) {

            val cell = MasterData.MasterCell(
                data.beaconId,
                data.cellId,
                data.cellName,
                data.inmatesCount,
                data.activityGroupId,
                data.inmateActivityGroupId,
                data.isGroupHead,
                data.beaconName,
                data.majorId,
                data.areaId,
                data.minorId
            )

            cellList.add(cell)
        }


        myRealm.commitTransaction()
        return cellList

    }

    fun getInmateList(myRealm: Realm, cellIds: Array<Int>?,beaconId:Int): ArrayList<MasterData.MasterInmate> {

        var inmateList = arrayListOf<MasterData.MasterInmate>()
        var results: RealmResults<MasterInmateRealmData>? = null

        results = if (cellIds != null) {
            myRealm.where(MasterInmateRealmData::class.java).`in`("cellId", cellIds).equalTo("beaconId",beaconId).findAll()
        } else {
            myRealm.where(MasterInmateRealmData::class.java).findAll()
        }


        if (!myRealm.isInTransaction())
            myRealm.beginTransaction()

        val items: Array<MasterInmateRealmData> = results.toTypedArray<MasterInmateRealmData>()

        for (data in items) {

            val inmate = MasterData.MasterInmate(
                data.cellId,
                data.inmateId,
                data.inmateName,
                data.photoUrl,
                data.beaconId,
                data.beaconName,
                data.majorId,
                data.areaId,
                data.minorId
            )

            inmateList.add(inmate)
        }


        myRealm.commitTransaction()
        return inmateList

    }


    class StoreHeadCountDataOffline(headCountResData: HeadCountResData, obj: RealmUtils) : AsyncTask<Void, Void, String>() {

        val headCountResData = headCountResData
        val obj = obj

        override fun doInBackground(vararg params: Void?): String? {

            obj.storeHeadCountData(Realm.getDefaultInstance(), headCountResData)

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()

            // ...
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // ...
        }
    }

    fun storeHeadCountData(myRealm: Realm, headCountResData: HeadCountResData) {

        Debug.e("storeHeadCountDataOffline", "started =====>")

        if (headCountResData.headCountSessions.isNullOrEmpty().not()) {
            updateOfflineHeadCountSessionList(headCountResData.headCountSessions, myRealm)
        }

    }

    private fun updateOfflineHeadCountSessionList(
        sessions: List<HeadCountResData.HeadCountSession>?,
        myRealm: Realm
    ) {
        if (sessions != null && sessions.size > 0) {

            myRealm.beginTransaction()
            myRealm.delete(HeadCountSessionRealmData::class.java)
            myRealm.commitTransaction()

            for (item in sessions) {
                val headCountData: HeadCountSessionRealmData? =
                    myRealm.where<HeadCountSessionRealmData>(HeadCountSessionRealmData::class.java)
                        .equalTo("headCountSessionConfigId", item.headCountSessionConfigId)
                        .findFirst()
                if (headCountData == null) {
                    addHeadCountSessionRealm(item, myRealm)
                } else {
                    myRealm.beginTransaction()
                    headCountData.sessionName = item.sessionName
                    headCountData.startTimeConfig = item.startTimeConfig
                    headCountData.endTimeConfig = item.endTimeConfig
                    headCountData.date = item.date
                    headCountData.expectedCount = item.expectedCount
                    headCountData.actualCount = item.actualCount
                    headCountData.status = item.status
                    headCountData.displayOrder = item.displayOrder
                    headCountData.isOnDemand = item.isOnDemand
                    myRealm.commitTransaction()
                }
            }
        }
    }

    fun addHeadCountSessionRealm(data: HeadCountResData.HeadCountSession, myRealm: Realm) {

        myRealm.beginTransaction()

        val headCountSession: HeadCountSessionRealmData =
            myRealm.createObject<HeadCountSessionRealmData>(HeadCountSessionRealmData::class.java)

        headCountSession.headCountSessionConfigId = data.headCountSessionConfigId
        headCountSession.sessionName = data.sessionName
        headCountSession.startTimeConfig = data.startTimeConfig
        headCountSession.endTimeConfig = data.endTimeConfig
        headCountSession.date = data.date
        headCountSession.expectedCount = data.expectedCount
        headCountSession.actualCount = data.actualCount
        headCountSession.status = data.status
        headCountSession.displayOrder = data.displayOrder
        headCountSession.isOnDemand = data.isOnDemand

        myRealm.commitTransaction()
    }

    fun getHeadCountSession(myRealm: Realm): List<HeadCountResData.HeadCountSession> {
        var headCountSession = arrayListOf<HeadCountResData.HeadCountSession>()

        var results: RealmResults<HeadCountSessionRealmData> = myRealm.where(HeadCountSessionRealmData::class.java).findAll()

        if (!myRealm.isInTransaction())
            myRealm.beginTransaction()

        val items: Array<HeadCountSessionRealmData> = results.toTypedArray<HeadCountSessionRealmData>()

        for (data in items) {
            headCountSession.add(HeadCountResData.HeadCountSession(data.headCountSessionConfigId,data.sessionName,data.startTimeConfig,data.endTimeConfig,data.date,data.expectedCount,data.actualCount,data.status,data.displayOrder,data.isOnDemand))
        }

        myRealm.commitTransaction()
        return headCountSession

    }

    fun getInsertedHeadCountList(
        myRealm: Realm,
        sessionId:Int,
        cellIds: Array<Int>?
    ): ArrayList<InsertHeadCountData> {

        var insertedHeadCountList = arrayListOf<InsertHeadCountData>()
        var results: RealmResults<InsertHeadCountRealmData>? = null

        results = if (cellIds != null) {
            myRealm.where(InsertHeadCountRealmData::class.java).equalTo("HeadCountSessionConfigId",sessionId).`in`("CellId", cellIds).findAll()
        } else if (sessionId != null) {
            myRealm.where(InsertHeadCountRealmData::class.java).equalTo("HeadCountSessionConfigId",sessionId).findAll()
        } else {
            myRealm.where(InsertHeadCountRealmData::class.java).findAll()
        }


        if (!myRealm.isInTransaction())
            myRealm.beginTransaction()

        val items: Array<InsertHeadCountRealmData> = results.toTypedArray<InsertHeadCountRealmData>()

        for (data in items) {

            val headCountData = InsertHeadCountData(data.Id,data.HeadCountSessionConfigId,data.StartDateTime,data.EndDateTime,data.LogDateTimeStamp,data.CellId,data.BeaconId,data.ActualCount,data.Comment,data.PhotoFileId,data.IsManualActivity,data.HeadCountDetail,data.Status,data.offlinePhotoPaths,data.isImageUploaded)

            insertedHeadCountList.add(headCountData)
        }


        myRealm.commitTransaction()
        return insertedHeadCountList

    }

    class StoreInsertedHeadCountCellInfoDataOffline(headCountCellInfoResData: HeadCountCellInfoResData, obj: RealmUtils) : AsyncTask<Void, Void, String>() {

        val headCountCellInfoResData = headCountCellInfoResData
        val obj = obj

        override fun doInBackground(vararg params: Void?): String? {

            obj.storeHeadCountCellInfoData(Realm.getDefaultInstance(), headCountCellInfoResData)

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()

            // ...
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // ...
        }
    }

    fun storeHeadCountCellInfoData(myRealm: Realm, headCountCellInfoResData: HeadCountCellInfoResData) {

        Debug.e("storeHeadCountCellInfoData", "started =====>")

        if (headCountCellInfoResData.cells.isNullOrEmpty().not()) {
            updateOfflineHeadCountSessionCellInfoList(headCountCellInfoResData.cells,headCountCellInfoResData.headCountSession!!, myRealm)
        }

    }

    private fun updateOfflineHeadCountSessionCellInfoList(
        sessions: List<HeadCountCellInfoResData.Cell>?,
        headCountCellInfoResData: HeadCountCellInfoResData.HeadCountSession,
        myRealm: Realm
    ) {
        if (sessions != null && sessions.size > 0) {

            for (item in sessions) {
                val headCountData: InsertHeadCountRealmData? =
                    myRealm.where<InsertHeadCountRealmData>(InsertHeadCountRealmData::class.java)
                        .equalTo("HeadCountSessionConfigId", headCountCellInfoResData.headCountSessionConfigId)
                        .equalTo("CellId", item.cellId)
                        .findFirst()
                if (headCountData == null) {
                    addInsertedHeadCountSessionCellInfoRealm(item, headCountCellInfoResData,myRealm)
                } else {
                    myRealm.beginTransaction()
                    headCountData.ActualCount = item.actualCount
                    myRealm.commitTransaction()
                }
            }
        }
    }

    fun addInsertedHeadCountSessionCellInfoRealm(cellData: HeadCountCellInfoResData.Cell, headCountCellInfoResData: HeadCountCellInfoResData.HeadCountSession,myRealm: Realm) {

        myRealm.beginTransaction()

        val insertHeadCountRealmData: InsertHeadCountRealmData =
            myRealm.createObject<InsertHeadCountRealmData>(InsertHeadCountRealmData::class.java)

        insertHeadCountRealmData.Id = Date().time
        insertHeadCountRealmData.HeadCountSessionConfigId =
            headCountCellInfoResData!!.headCountSessionConfigId
        insertHeadCountRealmData.CellId = cellData!!.cellId
        insertHeadCountRealmData.LogDateTimeStamp =
            Utils.parseTime(Date().time, Constant.SENDING_DATE_FORMATE)
        insertHeadCountRealmData.StartDateTime =
            Utils.parseTime(Date().time, Constant.SENDING_DATE_FORMATE)
        insertHeadCountRealmData.EndDateTime =
            Utils.parseTime(Date().time, Constant.SENDING_DATE_FORMATE)
        insertHeadCountRealmData.ActualCount = cellData.actualCount
        insertHeadCountRealmData.Status = Constant.STATUS_UPLOADED

        myRealm.commitTransaction()
    }*/
}