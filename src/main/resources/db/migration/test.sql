
select dr.reading_time, case  * from device_readings dr
left join devices d on dr.device_id = d.id
where d.device_type = #{deviceType}
and DATE(dr.reading_time)= #{date}
group by dr.reading_time