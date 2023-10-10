create table IF NOT EXISTS peer
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

create index peer_updateTime_index
    on peer (updateTime);

create index peer_createTime_index
    on peer (createTime);

create index peer_passkey_index
    on peer (passkey);

create index peer_peerId_index
    on peer (peerId);

------------------------------------------------------------


