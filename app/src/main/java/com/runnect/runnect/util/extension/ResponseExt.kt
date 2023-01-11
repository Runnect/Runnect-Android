fun Record.toData(): RecordInfoDTO {
    return RecordInfoDTO(
        id = id,
        img = image,
        title = title,
        location = departure.region + ' ' + departure.city,
        date = (createdAt.split(" ")[0]).replace("-", "."),
        distance = distance.toString(),
        time = timeConvert(time),
        pace = paceConvert(pace)
    )
}
