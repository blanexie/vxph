create table IF NOT EXISTS Peer
(
    passkey       TEXT              not null,
    peerId        TEXT              not null,
    infoHash      TEXT              not null,
    remoteAddress TEXT              not null,
    port          INTEGER,
    downloaded    TEXT              not null,
    "left"        TEXT              not null,
    uploaded      TEXT              not null,
    event         TEXT,
    status        INTEGER default 0 not null,
    createTime    TEXT              not null,
    updateTime    TEXT              not null,
    constraint peer_pk primary key (infoHash, passkey, peerId)
);

create index if not exists peer_updateTime_index
    on Peer (updateTime);

create index if not exists peer_createTime_index
    on Peer (createTime);

create index if not exists peer_passkey_index
    on Peer (passkey);

create index if not exists peer_peerId_index
    on Peer (peerId);

------------------------------------------------------------

create table IF NOT EXISTS UserTorrent
(
    passkey    TEXT              not null,
    infoHash   TEXT              not null,
    userId     INTEGER           not null,
    peerId     TEXT,

    status     INTEGER default 0 not null,
    createTime TEXT              not null,
    updateTime TEXT              not null,
    constraint UserTorrent_pk primary key (passkey, infoHash)
);

create index if not exists UserTorrent_updateTime_index
    on UserTorrent (updateTime);

create index if not exists UserTorrent_createTime_index
    on UserTorrent (createTime);

create index if not exists UserTorrent_infoHash_index
    on UserTorrent (infoHash);

create index if not exists UserTorrent_userId_index
    on UserTorrent (userId);

------------------------------------------------------------


create table IF NOT EXISTS User
(
    id         INTEGER           not null
        constraint User_pk primary key autoincrement,
    name       TEXT,
    email      TEXT,
    password   TEXT,
    sex        INTEGER default 0,
    inviteId   INTEGER,

    status     INTEGER default 0 not null,
    createTime TEXT              not null,
    updateTime TEXT              not null
);

create unique index if not exists User_name_index
    on User (name);

create unique index if not exists User_email_index
    on User (email);

create index if not exists User_updateTime_index
    on User (updateTime);

create index if not exists User_createTime_index
    on User (createTime);


INSERT OR IGNORE INTO User(id, name, email, password, sex, inviteId, status, createTime, updateTime)
VALUES (1, 'admin', 'admin@vxph.com', 'xLnnhrT1nld%', 1, 0, 0, '2023-10-31 00:00:00', '2023-10-31 00:00:00');

------------------------------------------------------------


create table IF NOT EXISTS Code
(
    code       TEXT              not null
        constraint Code_pk primary key,
    name       TEXT              not null,
    content    TEXT              not null,

    status     INTEGER default 0 not null,
    createTime TEXT              not null,
    updateTime TEXT              not null
);

create unique index if not exists Code_name_index
    on Code (name);

create index if not exists User_updateTime_index
    on User (updateTime);

create index if not exists User_createTime_index
    on User (createTime);


INSERT OR IGNORE INTO Code(code, name, content, status, createTime, updateTime)
VALUES ('role_path_manage', '接口角色权限管理',  '[{"name":"匿名访问的接口","path":"/ddns/findLocalIp","role":"Anonymous"},{"name":"ddns管理相关接口","path":"/ddns/*","role":"superAdmin"}]',  0, '2023-10-31 00:00:00', '2023-10-31 00:00:00');

------------------------------------------------------------
create table IF NOT EXISTS Account
(
    id          INTEGER                  not null
        constraint Invite_pk primary key autoincrement,
    userId      INTEGER                  not null,
    role        TEXT    default 'normal' not null,
    downloaded  TEXT    default '0'      not null,
    uploaded    TEXT    default '0'      not null,
    integral    INTEGER default 0        not null,
    level       INTEGER default 1        not null,
    inviteCount INTEGER default 0        not null,

    status      INTEGER default 0        not null,
    createTime  TEXT                     not null,
    updateTime  TEXT                     not null
);

create unique index if not exists Account_userId_index
    on Account (userId);

create index if not exists Account_updateTime_index
    on Account (updateTime);

create index if not exists Account_createTime_index
    on Account (createTime);

INSERT OR IGNORE INTO Account(id, userId, role, downloaded, uploaded, integral, level, inviteCount, status, createTime,
                              updateTime)
VALUES (1, 1, 'superAdmin', '0', '0', 1, 1, 1, 0, '2023-10-31 00:00:00', '2023-10-31 00:00:00');
------------------------------------------------------------


create table IF NOT EXISTS Invite
(
    id         INTEGER           not null
        constraint Invite_pk primary key autoincrement,
    sender     INTEGER           not null,
    code       TEXT              not null,
    email      TEXT              not null,
    expire     TEXT              not null,
    acceptTime TEXT,

    status     INTEGER default 0 not null,
    createTime TEXT              not null,
    updateTime TEXT              not null
);

create unique index if not exists Invite_name_index
    on Invite (sender);

create unique index if not exists Invite_email_index
    on Invite (email);

create index if not exists Invite_updateTime_index
    on Invite (updateTime);

create index if not exists Invite_createTime_index
    on Invite (createTime);


------------------------------------------------------------

create table IF NOT EXISTS Torrent
(
    infoHash     TEXT              not null
        constraint Torrent_pk primary key,
    name         TEXT              not null,
    length       TEXT              not null,
    `comment`    TEXT    default '',
    files        TEXT              not null,
    creationDate TEXT              not null,
    pieceLength  TEXT              not null,
    publisher    TEXT    default '',
    publisherUrl TEXT    default '',
    singleFile   INTEGER           not null,

    status       INTEGER default 0 not null,
    createTime   TEXT              not null,
    updateTime   TEXT              not null
);

create index if not exists Torrent_updateTime_index
    on Torrent (updateTime);

create index if not exists Torrent_createTime_index
    on Torrent (createTime);

------------------------------------------------------------

create table IF NOT EXISTS DomainRecord
(
    recordId   TEXT              not null
        constraint DomainRecord_pk primary key,
    type       TEXT              not null,
    rr         TEXT              not null,
    `value`    TEXT              not null,
    domainName TEXT              not null,
    ttl        INTEGER           not null,
    remark     TEXT,

    createTime TEXT              not null,
    updateTime TEXT              not null,
    status     INTEGER default 1 not null
);


create index if not exists DomainRecord_domainName_index
    on DomainRecord (domainName);

create index if not exists DomainRecord_updateTime_index
    on DomainRecord (updateTime);

create index if not exists DomainRecord_createTime_index
    on DomainRecord (createTime);
