package com.chao.cloud.common.config.redis;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public interface IRedisService {

	/**
	 * 删除key
	 * @param key key
	 * @return 删除的结果
	 */
	Boolean delete(String key);

	/**
	 * 批量删除key
	 * @param keys keys
	 * @return 删除的个数
	 */
	Long delete(Collection<String> keys);

	/**
	 * 序列化key
	 * @param key key
	 * @return byte[]
	 */
	byte[] dump(String key);

	/**
	 * 是否存在key
	 * @param key key
	 * @return boolean
	 */
	Boolean hasKey(String key);

	/**
	 * 设置过期时间
	 * 
	 * @param key key
	 * @param timeout 过期时间
	 * @param unit 单位 {@link TimeUnit}
	 * @return boolean
	 */
	Boolean expire(String key, long timeout, TimeUnit unit);

	/**
	 * 设置过期时间
	 * @param key key
	 * @param date 过期的具体时间
	 * @return boolean
	 */
	Boolean expireAt(String key, Date date);

	/**
	 * 查找匹配的key
	 * @param pattern 规则
	 * @return Set
	 */
	Set<String> keys(String pattern);

	/**
	 * 将当前数据库的 key 移动到给定的数据库 db 当中
	 * @param key key
	 * @param dbIndex 索引
	 * @return boolean
	 */
	Boolean move(String key, int dbIndex);

	/**
	 * 移除 key 的过期时间，key 将持久保持
	 * @param key key
	 * @return boolean
	 */
	Boolean persist(String key);

	/**
	 * 返回 key 的剩余的过期时间
	 * @param key key
	 * @param unit 单位 {@link TimeUnit}
	 * @return 剩余的过期时间
	 */
	Long getExpire(String key, TimeUnit unit);

	/**
	 * 返回 key 的剩余的过期时间
	 * @param key key
	 * @return 剩余的过期时间
	 */
	Long getExpire(String key);

	/**
	 * 从当前数据库中随机返回一个 key
	 * @return key
	 */
	String randomKey();

	/**
	 * 修改 key 的名称
	 * @param oldKey oldKey
	 * @param newKey newKey
	 */
	void rename(String oldKey, String newKey);

	/**
	 * 仅当 newkey 不存在时，将 oldKey 改名为 newkey
	 * 
	 * @param oldKey oldKey
	 * @param newKey newKey
	 * @return boolean
	 */
	Boolean renameIfAbsent(String oldKey, String newKey);

	/**
	 * 返回 key 所储存的值的类型
	 * 
	 * @param key key
	 * @return {@link DataType}
	 */
	DataType type(String key);

	/** -------------------string相关操作--------------------- */
	/**
	 * 设置指定 key 的值
	 * @param key key
	 * @param value value
	 */
	void set(String key, String value);

	/**
	 * 获取指定 key 的值
	 * @param key key
	 * @return value
	 */
	String get(String key);

	/**
	 * 返回 key 中字符串值的子字符
	 * @param key key
	 * @param start 开始
	 * @param end 结束
	 * @return  子字符
	 */
	String getRange(String key, long start, long end);

	/**
	 * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
	 * @param key key
	 * @param value value
	 * @return  key的旧值
	 */
	String getAndSet(String key, String value);

	/**
	 * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)
	 * @param key key
	 * @param offset 偏移量
	 * @return boolean
	 */
	Boolean getBit(String key, long offset);

	/**
	 * 批量获取
	 * @param keys keys
	 * @return  List
	 */
	List<String> multiGet(Collection<String> keys);

	/**
	 * 设置ASCII码, 字符串'a'的ASCII码是97, 转为二进制是'01100001', 此方法是将二进制第offset位值变为value
	 * 
	 * @param key key
	 * @param offset  偏移量
	 * @param value   值,true为1, false为0
	 * @return boolean
	 */
	boolean setBit(String key, long offset, boolean value);

	/**
	 * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
	 * 
	 * @param key key
	 * @param value value
	 * @param timeout   过期时间
	 * @param unit  时间单位, 天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES
	 *            秒:TimeUnit.SECONDS 毫秒:TimeUnit.MILLISECONDS
	 */
	void setEx(String key, String value, long timeout, TimeUnit unit);

	/**
	 * 只有在 key 不存在时设置 key 的值
	 * 
	 * @param key key
	 * @param value value
	 * @return 之前已经存在返回false,不存在返回true
	 */
	boolean setIfAbsent(String key, String value);

	/**
	 * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
	 * 
	 * @param key key
	 * @param value value
	 * @param offset  从指定位置开始覆写
	 */
	void setRange(String key, String value, long offset);

	/**
	 * 获取字符串的长度
	 * 
	 * @param key key
	 * @return 长度
	 */
	Long size(String key);

	/**
	 * 批量添加
	 * @param maps maps
	 */
	void multiSet(Map<String, String> maps);

	/**
	 * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
	 * @param maps maps
	 * @return 之前已经存在返回false,不存在返回true
	 */
	boolean multiSetIfAbsent(Map<String, String> maps);

	/**
	 * 增加(自增长), 负数则为自减
	 * 
	 * @param key key
	 * @param increment 增量
	 * @return Long
	 */
	Long incrBy(String key, long increment);

	/**
	 * 
	 * @param key key
	 * @param increment 增量
	 * @return Double
	 */
	Double incrByFloat(String key, double increment);

	/**
	 * 追加到末尾
	 * 
	 * @param key key
	 * @param value value
	 * @return Integer
	 */
	Integer append(String key, String value);

	/** -------------------hash相关操作------------------------- */
	/**
	 * 获取存储在哈希表中指定字段的值
	 * 
	 * @param key key
	 * @param field field
	 * @return value
	 */
	Object hGet(String key, String field);

	/**
	 * 获取所有给定字段的值
	 * @param key key
	 * @return Map
	 */
	Map<Object, Object> hGetAll(String key);

	/**
	 * 获取所有给定字段的值
	 * 
	 * @param key key
	 * @param fields fields
	 * @return List
	 */
	List<Object> hMultiGet(String key, Collection<Object> fields);

	void hPut(String key, String hashKey, String value);

	void hPutAll(String key, Map<String, String> maps);

	/**
	 * 仅当hashKey不存在时才设置
	 * 
	 * @param key key
	 * @param hashKey hashKey
	 * @param value value
	 * @return boolean
	 */
	Boolean hPutIfAbsent(String key, String hashKey, String value);

	/**
	 * 删除一个或多个哈希表字段
	 * 
	 * @param key key
	 * @param fields fields
	 * @return Long
	 */
	Long hDelete(String key, Object... fields);

	/**
	 * 查看哈希表 key 中，指定的字段是否存在
	 * @param key key
	 * @param field field
	 * @return boolean
	 */
	boolean hExists(String key, String field);

	/**
	 * 为哈希表 key 中的指定字段的整数值加上增量 increment
	 * @param key key
	 * @param field field
	 * @param increment increment
	 * @return Long
	 */
	Long hIncrBy(String key, Object field, long increment);

	/**
	 * 为哈希表 key 中的指定字段的整数值加上增量 increment
	 * 
	 * @param key key
	 * @param field field
	 * @param delta delta
	 * @return Double
	 */
	Double hIncrByFloat(String key, Object field, double delta);

	/**
	 * 获取所有哈希表中的字段
	 * 
	 * @param key key
	 * @return Set
	 */
	Set<Object> hKeys(String key);

	/**
	 * 获取哈希表中字段的数量
	 * 
	 * @param key key
	 * @return Long
	 */
	Long hSize(String key);

	/**
	 * 获取哈希表中所有值
	 * 
	 * @param key key
	 * @return List
	 */
	List<Object> hValues(String key);

	/**
	 * 迭代哈希表中的键值对
	 * 
	 * @param key key
	 * @param options options
	 * @return Cursor
	 */
	Cursor<Map.Entry<Object, Object>> hScan(String key, ScanOptions options);

	/** ------------------------list相关操作---------------------------- */
	/**
	 * 通过索引获取列表中的元素
	 * 
	 * @param key key
	 * @param index index
	 * @return String
	 */
	String lIndex(String key, long index);

	/**
	 * 获取列表指定范围内的元素
	 * 
	 * @param key key
	 * @param start   开始位置, 0是开始位置
	 * @param end   结束位置, -1返回所有
	 * @return List
	 */
	List<String> lRange(String key, long start, long end);

	/**
	 * 存储在list头部
	 * 
	 * @param key key
	 * @param value value
	 * @return Long
	 */
	Long lLeftPush(String key, String value);

	/**
	 * 
	 * @param key key
	 * @param value value
	 * @return Long
	 */
	Long lLeftPushAll(String key, String... value);

	/**
	 * 
	 * @param key key
	 * @param value value
	 * @return Long
	 */
	Long lLeftPushAll(String key, Collection<String> value);

	/**
	 * 当list存在的时候才加入
	 * 
	 * @param key key
	 * @param value value
	 * @return Long
	 */
	Long lLeftPushIfPresent(String key, String value);

	/**
	 * 如果pivot存在,再pivot前面添加
	 * 
	 * @param key key
	 * @param pivot pivot
	 * @param value value
	 * @return Long
	 */
	Long lLeftPush(String key, String pivot, String value);

	/**
	 * 
	 * @param key key
	 * @param value value
	 * @return Long
	 */
	Long lRightPush(String key, String value);

	/**
	 * 
	 * @param key key
	 * @param value value
	 * @return Long
	 */
	Long lRightPushAll(String key, String... value);

	/**
	 * 
	 * @param key key
	 * @param value value
	 * @return Long
	 */
	Long lRightPushAll(String key, Collection<String> value);

	/**
	 * 为已存在的列表添加值
	 * @param key key
	 * @param value value
	 * @return Long
	 */
	Long lRightPushIfPresent(String key, String value);

	/**
	 * 在pivot元素的右边添加值
	 * 
	 * @param key key
	 * @param pivot pivot
	 * @param value value
	 * @return Long
	 */
	Long lRightPush(String key, String pivot, String value);

	/**
	 * 通过索引设置列表元素的值
	 * @param key key
	 * @param index   位置
	 * @param value value
	 */
	void lSet(String key, long index, String value);

	/**
	 * 移出并获取列表的第一个元素
	 * @param key key
	 * @return 删除的元素
	 */
	String lLeftPop(String key);

	/**
	 * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
	 * @param key key
	 * @param timeout 等待时间
	 * @param unit  时间单位
	 * @return String
	 */
	String lBLeftPop(String key, long timeout, TimeUnit unit);

	/**
	 * 移除并获取列表最后一个元素
	 * @param key key
	 * @return 删除的元素
	 */
	String lRightPop(String key);

	/**
	 * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
	 * @param key key
	 * @param timeout 等待时间
	 * @param unit 时间单位
	 * @return String
	 */
	String lBRightPop(String key, long timeout, TimeUnit unit);

	/**
	 * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
	 * @param sourceKey sourceKey
	 * @param destinationKey destinationKey
	 * @return String
	 */
	String lRightPopAndLeftPush(String sourceKey, String destinationKey);

	/**
	 * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
	 * @param sourceKey sourceKey
	 * @param destinationKey destinationKey
	 * @param timeout 等待时间
	 * @param unit 时间单位
	 * @return String
	 */
	String lBRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit);

	/**
	 * 删除集合中值等于value得元素
	 * @param key key
	 * @param index
	 *            index 等于0, 删除所有值等于value的元素; index 大于0, 从头部开始删除第一个值等于value的元素;
	 *            index 小于0, 从尾部开始删除第一个值等于value的元素;
	 * @param value value
	 * @return Long
	 */
	Long lRemove(String key, long index, String value);

	/**
	 * 裁剪list
	 * @param key key
	 * @param start start
	 * @param end end
	 */
	void lTrim(String key, long start, long end);

	/**
	 * 获取列表长度
	 * @param key key
	 * @return Long
	 */
	Long lLen(String key);

	/** --------------------set相关操作-------------------------- */
	/**
	 * set添加元素
	 * 
	 * @param key key
	 * @param values values
	 * @return Long
	 */
	Long sAdd(String key, String... values);

	/**
	 * set移除元素
	 * 
	 * @param key key
	 * @param values values
	 * @return Long
	 */
	Long sRemove(String key, Object... values);

	/**
	 * 移除并返回集合的一个随机元素
	 * @param key key
	 * @return String
	 */
	String sPop(String key);

	/**
	 * 将元素value从一个集合移到另一个集合
	 * 
	 * @param key key
	 * @param value value
	 * @param destKey destKey
	 * @return  Boolean
	 */
	Boolean sMove(String key, String value, String destKey);

	/**
	 * 获取集合的大小
	 * @param key key
	 * @return Long
	 */
	Long sSize(String key);

	/**
	 * 判断集合是否包含value
	 * @param key key
	 * @param value value
	 * @return Boolean
	 */
	Boolean sIsMember(String key, Object value);

	/**
	 * 获取两个集合的交集
	 * @param key key
	 * @param otherKey otherKey
	 * @return Set
	 */
	Set<String> sIntersect(String key, String otherKey);

	/**
	 * 获取key集合与多个集合的交集
	 * @param key key
	 * @param otherKeys otherKeys
	 * @return Set
	 */
	Set<String> sIntersect(String key, Collection<String> otherKeys);

	/**
	 * key集合与otherKey集合的交集存储到destKey集合中
	 * @param key key
	 * @param otherKey otherKey
	 * @param destKey destKey
	 * @return Long
	 */
	Long sIntersectAndStore(String key, String otherKey, String destKey);

	/**
	 * key集合与多个集合的交集存储到destKey集合中
	 * @param key key
	 * @param otherKeys otherKeys
	 * @param destKey destKey
	 * @return Long
	 */
	Long sIntersectAndStore(String key, Collection<String> otherKeys, String destKey);

	/**
	 * 获取两个集合的并集
	 * @param key key
	 * @param otherKeys otherKeys
	 * @return Set
	 */
	Set<String> sUnion(String key, String otherKeys);

	/**
	 * 获取key集合与多个集合的并集
	 * @param key key
	 * @param otherKeys otherKeys
	 * @return Set
	 */
	Set<String> sUnion(String key, Collection<String> otherKeys);

	/**
	 * key集合与otherKey集合的并集存储到destKey中
	 * @param key key
	 * @param otherKey otherKey
	 * @param destKey destKey
	 * @return Long
	 */
	Long sUnionAndStore(String key, String otherKey, String destKey);

	/**
	 * key集合与多个集合的并集存储到destKey中
	 * @param key key
	 * @param otherKeys otherKeys
	 * @param destKey destKey
	 * @return Long
	 */
	Long sUnionAndStore(String key, Collection<String> otherKeys, String destKey);

	/**
	 * 获取两个集合的差集
	 * @param key key
	 * @param otherKey otherKey
	 * @return Set
	 */
	Set<String> sDifference(String key, String otherKey);

	/**
	 * 获取key集合与多个集合的差集
	 * @param key key
	 * @param otherKeys otherKeys
	 * @return Set
	 */
	Set<String> sDifference(String key, Collection<String> otherKeys);

	/**
	 * key集合与otherKey集合的差集存储到destKey中
	 * @param key key
	 * @param otherKey otherKey
	 * @param destKey destKey
	 * @return Long
	 */
	Long sDifference(String key, String otherKey, String destKey);

	/**
	 * key集合与多个集合的差集存储到destKey中
	 * @param key key
	 * @param otherKeys otherKeys
	 * @param destKey destKey
	 * @return Long
	 */
	Long sDifference(String key, Collection<String> otherKeys, String destKey);

	/**
	 * 获取集合所有元素
	 * @param key key
	 * @return Set
	 */
	Set<String> setMembers(String key);

	/**
	 * 随机获取集合中的一个元素
	 * @param key key
	 * @return String
	 */
	String sRandomMember(String key);

	/**
	 * 随机获取集合中count个元素
	 * @param key key
	 * @param count count
	 * @return List
	 */
	List<String> sRandomMembers(String key, long count);

	/**
	 * 随机获取集合中count个元素并且去除重复的
	 * @param key key
	 * @param count count
	 * @return Set
	 */
	Set<String> sDistinctRandomMembers(String key, long count);

	/**
	 * 
	 * @param key key
	 * @param options options
	 * @return Cursor
	 */
	Cursor<String> sScan(String key, ScanOptions options);

	/**------------------zSet相关操作--------------------------------*/
	/**
	 * 添加元素,有序集合是按照元素的score值由小到大排列
	 * 
	 * @param key key
	 * @param value value
	 * @param score score
	 * @return Cursor
	 */
	Boolean zAdd(String key, String value, double score);

	/**
	 * 
	 * @param key key
	 * @param values values
	 * @return Long
	 */
	Long zAdd(String key, Set<TypedTuple<String>> values);

	/**
	 * 
	 * @param key key
	 * @param values values
	 * @return Long
	 */
	Long zRemove(String key, Object... values);

	/**
	 * 增加元素的score值，并返回增加后的值
	 * 
	 * @param key key
	 * @param value value
	 * @param delta delta
	 * @return Double
	 */
	Double zIncrementScore(String key, String value, double delta);

	/**
	 * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
	 * @param key key
	 * @param value value
	 * @return 0表示第一位
	 */
	Long zRank(String key, Object value);

	/**
	 * 返回元素在集合的排名,按元素的score值由大到小排列
	 * @param key key
	 * @param value value
	 * @return Long
	 */
	Long zReverseRank(String key, Object value);

	/**
	 * 获取集合的元素, 从小到大排序
	 * 
	 * @param key key
	 * @param start  开始位置
	 * @param end   结束位置, -1查询所有
	 * @return Set
	 */
	Set<String> zRange(String key, long start, long end);

	/**
	 * 获取集合元素, 并且把score值也获取
	 * @param key key
	 * @param start  开始位置
	 * @param end  结束位置, -1查询所有
	 * @return Set
	 */
	Set<TypedTuple<String>> zRangeWithScores(String key, long start, long end);

	/**
	 * 根据Score值查询集合元素
	 * @param key key
	 * @param min 最小值
	 * @param max 最大值
	 * @return Set
	 */
	Set<String> zRangeByScore(String key, double min, double max);

	/**
	 * 根据Score值查询集合元素, 从小到大排序
	 * @param key key
	 * @param min 最小值
	 * @param max 最大值
	 * @return Set
	 */
	Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max);

	/**
	 * 
	 * @param key key
	 * @param min 最小值
	 * @param max 最大值
	 * @param start  开始位置
	 * @param end  结束位置, -1查询所有
	 * @return Set
	 */
	Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max, long start, long end);

	/**
	 * 获取集合的元素, 从大到小排序
	 * @param key key
	 * @param start  开始位置
	 * @param end  结束位置, -1查询所有
	 * @return Set
	 */
	Set<String> zReverseRange(String key, long start, long end);

	/**
	 * 获取集合的元素, 从大到小排序, 并返回score值
	 * @param key key
	 * @param start  开始位置
	 * @param end  结束位置, -1查询所有
	 * @return Set
	 */
	Set<TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end);

	/**
	 * 根据Score值查询集合元素, 从大到小排序
	 * @param key key
	 * @param min 最小值
	 * @param max 最大值
	 * @return Set
	 */
	Set<String> zReverseRangeByScore(String key, double min, double max);

	/**
	 * 根据Score值查询集合元素, 从大到小排序
	 * @param key key
	 * @param min 最小值
	 * @param max 最大值
	 * @return Set
	 */
	Set<TypedTuple<String>> zReverseRangeByScoreWithScores(String key, double min, double max);

	/**
	 * 
	 * @param key key
	 * @param min 最小值
	 * @param max 最大值
	 * @param start  开始位置
	 * @param end  结束位置, -1查询所有
	 * @return Set
	 */
	Set<String> zReverseRangeByScore(String key, double min, double max, long start, long end);

	/**
	 * 根据score值获取集合元素数量
	 * @param key key
	 * @param min 最小值
	 * @param max 最大值
	 * @return Long
	 */
	Long zCount(String key, double min, double max);

	/**
	 * 获取集合大小
	 * @param key key
	 * @return Long
	 */
	Long zSize(String key);

	/**
	 * 获取集合大小
	 * @param key key
	 * @return Long
	 */
	Long zZCard(String key);

	/**
	 * 获取集合中value元素的score值
	 * @param key key
	 * @param value value
	 * @return Double
	 */
	Double zScore(String key, Object value);

	/**
	 * 移除指定索引位置的成员
	 * @param key key
	 * @param start start
	 * @param end end
	 * @return Long
	 */
	Long zRemoveRange(String key, long start, long end);

	/**
	 * 根据指定的score值的范围来移除成员
	 * @param key key
	 * @param min min
	 * @param max max
	 * @return Long
	 */
	Long zRemoveRangeByScore(String key, double min, double max);

	/**
	 * 获取key和otherKey的并集并存储在destKey中
	 * @param key key
	 * @param otherKey otherKey
	 * @param destKey destKey
	 * @return Long
	 */
	Long zUnionAndStore(String key, String otherKey, String destKey);

	/**
	 * 
	 * @param key key
	 * @param otherKeys otherKeys
	 * @param destKey destKey
	 * @return Long
	 */
	Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey);

	/**
	 * 交集
	 * @param key key
	 * @param otherKey otherKey
	 * @param destKey destKey
	 * @return Long
	 */
	Long zIntersectAndStore(String key, String otherKey, String destKey);

	/**
	 * 交集
	 * 
	 * @param key key
	 * @param otherKeys otherKeys
	 * @param destKey destKey
	 * @return Long
	 */
	Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey);

	/**
	 * 有序集
	 * @param key key
	 * @param options options
	 * @return  Cursor
	 */
	Cursor<TypedTuple<String>> zScan(String key, ScanOptions options);
}
