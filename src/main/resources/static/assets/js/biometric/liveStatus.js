function updateMessCounts(messId) {
    fetch(contextPath + "/liveStatus/messCount/" + messId)
}