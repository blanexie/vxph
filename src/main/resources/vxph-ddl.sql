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
